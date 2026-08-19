package com.aegis.operations.integration.fema;

import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.store.InMemoryOperationsStore;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FemaIngestionServiceTest {
    @Test
    void repeatedIngestionUpdatesExistingDeclarationInsteadOfDuplicating() {
        InMemoryOperationsStore store = new InMemoryOperationsStore();
        FemaDisasterClient client = mock(FemaDisasterClient.class);
        FemaIngestionService service = service(client, store);
        when(client.fetchRecentDeclarations(any(), any(LocalDate.class))).thenReturn(List.of(
                FemaDisasterMapperTest.record(4926, "Camden County"),
                FemaDisasterMapperTest.record(4926, "Burlington County")));

        FemaIngestionResult first = service.refresh();
        FemaIngestionResult second = service.refresh();

        assertThat(first.created()).isEqualTo(1);
        assertThat(second.created()).isZero();
        assertThat(second.updated()).isEqualTo(1);
        assertThat(store.searchDeclarations(new com.aegis.operations.store.DeclarationSearchCriteria(null, null, null,
                null))).hasSize(1);
    }

    @Test
    void failedRefreshDoesNotDeleteExistingDeclarations() {
        InMemoryOperationsStore store = new InMemoryOperationsStore();
        FemaDisasterClient client = mock(FemaDisasterClient.class);
        FemaIngestionService service = service(client, store);
        store.saveDeclaration(new DisasterDeclaration("FEMA-4926", 4926, "DR", "NJ", "Flood", "Flood", null, null,
                null, List.of("Camden County"), false, true, false, "fema", "4926", null, null));
        when(client.fetchRecentDeclarations(any(), any(LocalDate.class))).thenThrow(new IllegalStateException("FEMA unavailable"));

        assertThatThrownBy(service::refresh).isInstanceOf(IllegalStateException.class);
        assertThat(store.searchDeclarations(new com.aegis.operations.store.DeclarationSearchCriteria(null, null, null,
                null))).hasSize(1);
    }

    private static FemaIngestionService service(FemaDisasterClient client, InMemoryOperationsStore store) {
        FemaIngestionProperties properties = new FemaIngestionProperties();
        properties.setStates(List.of("NJ"));
        return new FemaIngestionService(client, new FemaDisasterMapper(), properties, store,
                new com.aegis.operations.service.IncidentDeclarationCorrelationService());
    }
}
