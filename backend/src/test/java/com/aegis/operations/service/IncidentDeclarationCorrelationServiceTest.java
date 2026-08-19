package com.aegis.operations.service;

import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentDeclarationLink;
import com.aegis.operations.model.IncidentKind;
import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.model.IncidentStatus;
import com.aegis.operations.model.Severity;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IncidentDeclarationCorrelationServiceTest {
    private final IncidentDeclarationCorrelationService service = new IncidentDeclarationCorrelationService();

    @Test
    void correlatesByStateHazardAndTime() {
        IncidentDeclarationLink link = service.correlate(incident(IncidentKind.FLOOD, "NJ",
                Instant.parse("2026-08-01T12:00:00Z")), declaration("NJ", "Flood"));

        assertThat(link).isNotNull();
        assertThat(link.getConfidence()).isGreaterThanOrEqualTo(0.95);
        assertThat(link.getReasons()).contains("same_state", "compatible_hazard", "overlapping_time_window");
    }

    @Test
    void hazardCompatibilityCanCreateAConservativeMatchWithStateAndTime() {
        IncidentDeclarationLink link = service.correlate(incident(IncidentKind.WEATHER, "NJ",
                Instant.parse("2026-08-01T12:00:00Z")), declaration("NJ", "Severe Storm"));

        assertThat(link).isNotNull();
        assertThat(link.getReasons()).contains("compatible_hazard");
    }

    @Test
    void rejectsLowConfidenceStateOnlyMatch() {
        IncidentDeclarationLink link = service.correlate(incident(IncidentKind.OUTAGE, "NJ",
                Instant.parse("2026-08-01T12:00:00Z")), declaration("NJ", "Fire"));

        assertThat(link).isNull();
    }

    private static Incident incident(IncidentKind kind, String state, Instant reportedAt) {
        return new Incident("NWS-1", "Flood Warning", kind, Severity.HIGH, "Camden County", state,
                IncidentStatus.MONITORING, reportedAt, null, "Description", List.of(), List.of(), IncidentSource.NWS,
                "source-1", null, reportedAt, reportedAt);
    }

    private static DisasterDeclaration declaration(String state, String incidentType) {
        return new DisasterDeclaration("FEMA-4926", 4926, "DR", state, "Severe Storms and Flooding", incidentType,
                Instant.parse("2026-08-03T00:00:00Z"), LocalDate.parse("2026-07-28"),
                LocalDate.parse("2026-08-02"), List.of("Camden County"), true, true, false, "fema", "4926",
                Instant.parse("2026-08-03T00:00:00Z"), Instant.parse("2026-08-18T04:00:00Z"));
    }
}
