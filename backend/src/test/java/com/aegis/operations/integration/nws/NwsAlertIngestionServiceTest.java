package com.aegis.operations.integration.nws;

import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.store.InMemoryOperationsStore;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NwsAlertIngestionServiceTest {
    @Test
    void refreshCreatesAndUpdatesNwsIncidentsWithoutTouchingDemoData() {
        InMemoryOperationsStore store = new InMemoryOperationsStore();
        NwsAlertClient client = mock(NwsAlertClient.class);
        NwsIngestionProperties properties = new NwsIngestionProperties();
        properties.setAreas(List.of("NJ"));
        NwsAlertIngestionService service = new NwsAlertIngestionService(client, new NwsAlertIncidentMapper(),
                properties, store);

        when(client.fetchActiveAlerts(List.of("NJ"))).thenReturn(List.of(alert("nws-1"), alert("nws-2")));

        NwsIngestionResult result = service.refresh();

        assertThat(result.status()).isEqualTo("success");
        assertThat(result.fetched()).isEqualTo(2);
        assertThat(result.created()).isEqualTo(2);
        assertThat(store.incidentSnapshotsBySource(IncidentSource.DEMO)).hasSize(4);
        assertThat(store.incidentSnapshotsBySource(IncidentSource.NWS)).hasSize(2);
    }

    @Test
    void successfulRefreshRemovesStaleNwsIncidentsOnly() {
        InMemoryOperationsStore store = new InMemoryOperationsStore();
        NwsAlertClient client = mock(NwsAlertClient.class);
        NwsIngestionProperties properties = new NwsIngestionProperties();
        properties.setAreas(List.of("NJ"));
        NwsAlertIngestionService service = new NwsAlertIngestionService(client, new NwsAlertIncidentMapper(),
                properties, store);

        when(client.fetchActiveAlerts(List.of("NJ")))
                .thenReturn(List.of(alert("nws-1"), alert("nws-2")))
                .thenReturn(List.of(alert("nws-2")));

        service.refresh();
        NwsIngestionResult result = service.refresh();

        assertThat(result.created()).isZero();
        assertThat(result.updated()).isEqualTo(1);
        assertThat(result.removed()).isEqualTo(1);
        assertThat(store.incidentSnapshotsBySource(IncidentSource.DEMO)).hasSize(4);
        assertThat(store.incidentSnapshotsBySource(IncidentSource.NWS)).hasSize(1);
    }

    @Test
    void failedRefreshDoesNotDeleteExistingNwsIncidents() {
        InMemoryOperationsStore store = new InMemoryOperationsStore();
        NwsAlertClient client = mock(NwsAlertClient.class);
        NwsIngestionProperties properties = new NwsIngestionProperties();
        properties.setAreas(List.of("NJ"));
        NwsAlertIngestionService service = new NwsAlertIngestionService(client, new NwsAlertIncidentMapper(),
                properties, store);

        when(client.fetchActiveAlerts(List.of("NJ")))
                .thenReturn(List.of(alert("nws-1")))
                .thenThrow(new IllegalStateException("NWS unavailable"));

        service.refresh();

        assertThatThrownBy(service::refresh).isInstanceOf(IllegalStateException.class);
        assertThat(store.incidentSnapshotsBySource(IncidentSource.NWS)).hasSize(1);
        assertThat(service.status().lastError()).contains("NWS unavailable");
    }

    private static NwsAlertEnvelope alert(String id) {
        return new NwsAlertEnvelope("NJ", new NwsAlertFeature(id, new NwsAlertProperties(id, id,
                "Severe Thunderstorm Warning",
                "Severe Thunderstorm Warning for Test County", "Severe", "Likely", "Expected", "Test County",
                Instant.parse("2026-08-17T14:50:00Z"), Instant.parse("2026-08-17T14:55:00Z"),
                Instant.parse("2026-08-17T15:00:00Z"), Instant.parse("2026-08-17T18:00:00Z"),
                "Alert description.", "Follow local guidance.", id), null));
    }
}
