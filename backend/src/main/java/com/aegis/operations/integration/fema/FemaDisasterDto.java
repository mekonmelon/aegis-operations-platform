package com.aegis.operations.integration.fema;

import java.time.Instant;

public record FemaDisasterDto(
        Integer disasterNumber,
        String declarationType,
        String state,
        String declarationTitle,
        String incidentType,
        Instant declarationDate,
        Instant incidentBeginDate,
        Instant incidentEndDate,
        String designatedArea,
        Boolean iaProgramDeclared,
        Boolean paProgramDeclared,
        Boolean hmProgramDeclared,
        Instant lastRefresh) {
}
