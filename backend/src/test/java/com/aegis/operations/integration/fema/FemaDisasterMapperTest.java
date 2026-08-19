package com.aegis.operations.integration.fema;

import com.aegis.operations.model.DisasterDeclaration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FemaDisasterMapperTest {
    private final FemaDisasterMapper mapper = new FemaDisasterMapper();

    @Test
    void groupsMultipleAreaRecordsIntoOneDeclaration() {
        List<DisasterDeclaration> declarations = mapper.toDeclarations(List.of(
                record(4926, "Camden County"),
                record(4926, "Burlington County")), Instant.parse("2026-08-18T04:00:00Z"));

        assertThat(declarations).hasSize(1);
        assertThat(declarations.getFirst().getId()).isEqualTo("FEMA-4926");
        assertThat(declarations.getFirst().getDeclaredAreas()).containsExactly("Burlington County", "Camden County");
    }

    @Test
    void deterministicIdUsesDisasterNumber() {
        DisasterDeclaration declaration = mapper.toDeclarations(List.of(record(4926, "Camden County")),
                Instant.parse("2026-08-18T04:00:00Z")).getFirst();

        assertThat(declaration.getId()).isEqualTo("FEMA-4926");
        assertThat(declaration.getSourceId()).isEqualTo("4926");
        assertThat(declaration.getSource()).isEqualTo("fema");
    }

    static FemaDisasterDto record(int disasterNumber, String area) {
        return new FemaDisasterDto(disasterNumber, "DR", "NJ", "Severe Storms and Flooding", "Flood",
                Instant.parse("2026-08-01T00:00:00Z"), Instant.parse("2026-07-28T00:00:00Z"),
                Instant.parse("2026-08-02T00:00:00Z"), area, true, true, false,
                Instant.parse("2026-08-03T00:00:00Z"));
    }
}
