package com.aegis.operations.integration.fema;

import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentDeclarationLink;
import com.aegis.operations.store.DeclarationSearchCriteria;
import com.aegis.operations.store.IncidentSearchCriteria;
import com.aegis.operations.store.OperationsStore;
import com.aegis.operations.service.IncidentDeclarationCorrelationService;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FemaIngestionService {
    private final FemaDisasterClient client;
    private final FemaDisasterMapper mapper;
    private final FemaIngestionProperties properties;
    private final OperationsStore store;
    private final IncidentDeclarationCorrelationService correlationService;

    private FemaIngestionResult lastResult;
    private Instant lastAttempt;
    private Instant lastSuccessfulSync;
    private String lastError;

    public FemaIngestionService(FemaDisasterClient client, FemaDisasterMapper mapper,
            FemaIngestionProperties properties, OperationsStore store,
            IncidentDeclarationCorrelationService correlationService) {
        this.client = client;
        this.mapper = mapper;
        this.properties = properties;
        this.store = store;
        this.correlationService = correlationService;
    }

    public synchronized FemaIngestionResult refresh() {
        Instant startedAt = Instant.now();
        lastAttempt = startedAt;
        try {
            List<FemaDisasterDto> records = client.fetchRecentDeclarations(properties.effectiveStates(),
                    properties.earliestDeclarationDate());
            List<DisasterDeclaration> declarations = mapper.toDeclarations(records, startedAt);

            int created = 0;
            int updated = 0;
            int linksCreated = 0;
            for (DisasterDeclaration declaration : declarations) {
                if (store.declarationSnapshot(declaration.getId()).isPresent()) {
                    updated++;
                } else {
                    created++;
                }
                store.saveDeclaration(declaration);
                linksCreated += rebuildLinksForDeclaration(declaration);
            }
            store.updateLastUpdated();

            Instant completedAt = Instant.now();
            FemaIngestionResult result = new FemaIngestionResult("FEMA", records.size(), declarations.size(), created,
                    updated, linksCreated, startedAt, completedAt, "success", null);
            lastSuccessfulSync = completedAt;
            lastError = null;
            lastResult = result;
            return result;
        } catch (RuntimeException exception) {
            Instant completedAt = Instant.now();
            lastError = exception.getMessage();
            lastResult = new FemaIngestionResult("FEMA", 0, 0, 0, 0, 0, startedAt, completedAt, "failed",
                    lastError);
            throw exception;
        }
    }

    public synchronized FemaIngestionStatus status() {
        return new FemaIngestionStatus("FEMA", properties.effectiveStates(), lastAttempt, lastSuccessfulSync,
                lastResult, lastError);
    }

    private int rebuildLinksForDeclaration(DisasterDeclaration declaration) {
        store.deleteLinksForDeclaration(declaration.getId());
        List<Incident> incidents = store.searchIncidents(new IncidentSearchCriteria(null, null, null, null, null));
        int count = 0;
        for (Incident incident : incidents) {
            IncidentDeclarationLink link = correlationService.correlate(incident, declaration);
            if (link != null) {
                store.saveIncidentDeclarationLink(link);
                count++;
            }
        }
        return count;
    }
}
