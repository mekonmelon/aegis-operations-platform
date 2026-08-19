package com.aegis.operations.integration.nws;

import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.store.OperationsStore;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class NwsAlertIngestionService {
    private static final Logger logger = LoggerFactory.getLogger(NwsAlertIngestionService.class);

    private final NwsAlertClient client;
    private final NwsAlertIncidentMapper mapper;
    private final NwsIngestionProperties properties;
    private final OperationsStore store;

    private NwsIngestionResult lastResult;
    private Instant lastAttempt;
    private Instant lastSuccessfulSync;
    private String lastError;

    public NwsAlertIngestionService(NwsAlertClient client, NwsAlertIncidentMapper mapper,
            NwsIngestionProperties properties, OperationsStore store) {
        this.client = client;
        this.mapper = mapper;
        this.properties = properties;
        this.store = store;
    }

    public synchronized NwsIngestionResult refresh() {
        Instant startedAt = Instant.now();
        lastAttempt = startedAt;

        try {
            List<NwsAlertEnvelope> fetchedAlerts = client.fetchActiveAlerts(properties.effectiveAreas());
            Map<String, Incident> incomingBySourceId = normalize(fetchedAlerts, startedAt);
            Map<String, Incident> existingBySourceId = store.incidentSnapshotsBySource(IncidentSource.NWS).stream()
                    .collect(Collectors.toMap(Incident::getSourceId, Function.identity(), (first, ignored) -> first,
                            LinkedHashMap::new));

            int created = 0;
            int updated = 0;
            for (Incident incoming : incomingBySourceId.values()) {
                Incident existing = existingBySourceId.get(incoming.getSourceId());
                if (existing == null) {
                    created++;
                } else {
                    incoming.setAssignedResourceIds(existing.getAssignedResourceIds());
                    updated++;
                }
                store.saveIncident(incoming);
            }

            Set<String> activeSourceIds = incomingBySourceId.keySet();
            int removed = 0;
            for (Incident existing : existingBySourceId.values()) {
                if (!activeSourceIds.contains(existing.getSourceId())) {
                    store.deleteIncident(existing.getId());
                    removed++;
                }
            }

            if (created > 0 || updated > 0 || removed > 0) {
                store.updateLastUpdated();
            }

            Instant completedAt = Instant.now();
            NwsIngestionResult result = new NwsIngestionResult("NWS", incomingBySourceId.size(), created, updated,
                    removed, startedAt, completedAt, "success", null);
            lastSuccessfulSync = completedAt;
            lastError = null;
            lastResult = result;
            return result;
        } catch (RuntimeException exception) {
            Instant completedAt = Instant.now();
            lastError = exception.getMessage();
            lastResult = new NwsIngestionResult("NWS", 0, 0, 0, 0, startedAt, completedAt, "failed",
                    lastError);
            throw exception;
        }
    }

    public synchronized NwsIngestionStatus status() {
        return new NwsIngestionStatus("NWS", properties.isEnabled(), properties.isScheduledEnabled(),
                properties.effectiveAreas(), lastAttempt, lastSuccessfulSync, lastResult, lastError);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void refreshOnStartup() {
        if (!properties.isEnabled()) {
            return;
        }
        try {
            refresh();
        } catch (RuntimeException exception) {
            logger.warn("NWS startup ingestion failed: {}", exception.getMessage());
        }
    }

    @Scheduled(fixedDelayString = "#{@nwsIngestionProperties.effectiveRefreshIntervalMillis()}")
    public void scheduledRefresh() {
        if (!properties.isEnabled() || !properties.isScheduledEnabled()) {
            return;
        }
        try {
            refresh();
        } catch (RuntimeException exception) {
            logger.warn("Scheduled NWS ingestion failed: {}", exception.getMessage());
        }
    }

    private Map<String, Incident> normalize(List<NwsAlertEnvelope> alerts, Instant ingestedAt) {
        Map<String, Incident> incidents = new LinkedHashMap<>();
        for (NwsAlertEnvelope envelope : alerts) {
            NwsAlertFeature alert = envelope.alert();
            if (alert.properties() == null || !hasStableSourceId(alert)) {
                continue;
            }
            Incident incident = mapper.toIncident(alert, ingestedAt, envelope.area());
            incidents.putIfAbsent(incident.getSourceId(), incident);
        }
        return incidents;
    }

    private static boolean hasStableSourceId(NwsAlertFeature alert) {
        NwsAlertProperties properties = alert.properties();
        return StringUtils.hasText(properties.id()) || StringUtils.hasText(properties.atId())
                || StringUtils.hasText(alert.id());
    }
}
