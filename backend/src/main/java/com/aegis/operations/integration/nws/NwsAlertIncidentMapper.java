package com.aegis.operations.integration.nws;

import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentKind;
import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.model.IncidentStatus;
import com.aegis.operations.model.Severity;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class NwsAlertIncidentMapper {
    public Incident toIncident(NwsAlertFeature alert, Instant ingestedAt) {
        return toIncident(alert, ingestedAt, null);
    }

    public Incident toIncident(NwsAlertFeature alert, Instant ingestedAt, String state) {
        NwsAlertProperties properties = alert.properties();
        String sourceId = firstText(properties.id(), properties.atId(), alert.id());
        String title = firstText(properties.headline(), properties.event(), "National Weather Service Alert");
        Instant reportedAt = firstInstant(properties.onset(), properties.effective(), properties.sent(), ingestedAt);
        String sourceUrl = firstText(properties.web(), properties.atId(), alert.id());

        return new Incident(
                incidentId(sourceId),
                title,
                mapKind(properties.event()),
                mapSeverity(properties.severity()),
                firstText(properties.areaDesc(), "Affected forecast area"),
                state,
                IncidentStatus.MONITORING,
                reportedAt,
                null,
                description(properties),
                List.of(),
                List.of(),
                IncidentSource.NWS,
                sourceId,
                sourceUrl,
                firstInstant(properties.sent(), properties.effective(), reportedAt),
                ingestedAt);
    }

    public String incidentId(String sourceId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sourceId.getBytes(StandardCharsets.UTF_8));
            return "NWS-" + HexFormat.of().formatHex(hash, 0, 8).toUpperCase();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }

    Severity mapSeverity(String nwsSeverity) {
        if (!StringUtils.hasText(nwsSeverity)) {
            return Severity.LOW;
        }

        return switch (nwsSeverity.trim().toLowerCase()) {
            case "extreme" -> Severity.CRITICAL;
            case "severe" -> Severity.HIGH;
            case "moderate" -> Severity.MODERATE;
            case "minor" -> Severity.LOW;
            default -> Severity.LOW;
        };
    }

    IncidentKind mapKind(String event) {
        if (!StringUtils.hasText(event)) {
            return IncidentKind.WEATHER;
        }

        String normalized = event.toLowerCase();
        if (normalized.contains("flash flood warning")
                || normalized.contains("flood warning")
                || normalized.contains("flood advisory")) {
            return IncidentKind.FLOOD;
        }
        return IncidentKind.WEATHER;
    }

    private static String description(NwsAlertProperties properties) {
        String description = firstText(properties.description(), properties.instruction(), "No alert details provided.");
        if (!StringUtils.hasText(properties.instruction()) || description.contains(properties.instruction())) {
            return description;
        }
        return description + "\n\nInstruction: " + properties.instruction();
    }

    private static String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private static Instant firstInstant(Instant... values) {
        for (Instant value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
