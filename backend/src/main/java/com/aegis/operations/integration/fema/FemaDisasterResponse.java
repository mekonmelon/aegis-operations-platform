package com.aegis.operations.integration.fema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FemaDisasterResponse(
        @JsonProperty("DisasterDeclarationsSummaries") List<FemaDisasterDto> records) {
    public List<FemaDisasterDto> records() {
        return records == null ? List.of() : records;
    }
}
