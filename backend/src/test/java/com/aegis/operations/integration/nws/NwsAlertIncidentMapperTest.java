package com.aegis.operations.integration.nws;

import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentKind;
import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.model.Severity;
import java.time.Instant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NwsAlertIncidentMapperTest {
    private final NwsAlertIncidentMapper mapper = new NwsAlertIncidentMapper();

    @Test
    void mapsFloodAlertsConservatively() {
        Incident incident = mapper.toIncident(alert("https://api.weather.gov/alerts/abc", "Flash Flood Warning",
                "Severe"), Instant.parse("2026-08-17T15:00:00Z"));

        assertThat(incident.getId()).startsWith("NWS-");
        assertThat(incident.getKind()).isEqualTo(IncidentKind.FLOOD);
        assertThat(incident.getSeverity()).isEqualTo(Severity.HIGH);
        assertThat(incident.getSource()).isEqualTo(IncidentSource.NWS);
        assertThat(incident.getCoordinates()).isNull();
    }

    @Test
    void mapsNonFloodWeatherAlertsToWeatherKind() {
        Incident incident = mapper.toIncident(alert("https://api.weather.gov/alerts/red-flag", "Red Flag Warning",
                "Extreme"), Instant.parse("2026-08-17T15:00:00Z"));

        assertThat(incident.getKind()).isEqualTo(IncidentKind.WEATHER);
        assertThat(incident.getSeverity()).isEqualTo(Severity.CRITICAL);
    }

    private static NwsAlertFeature alert(String id, String event, String severity) {
        return new NwsAlertFeature(id, new NwsAlertProperties(id, id, event, event + " for Test County", severity,
                "Likely", "Expected", "Test County", Instant.parse("2026-08-17T14:50:00Z"),
                Instant.parse("2026-08-17T14:55:00Z"), Instant.parse("2026-08-17T15:00:00Z"),
                Instant.parse("2026-08-17T18:00:00Z"), "Alert description.", "Follow local guidance.", id), null);
    }
}
