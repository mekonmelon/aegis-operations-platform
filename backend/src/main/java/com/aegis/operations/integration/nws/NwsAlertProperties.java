package com.aegis.operations.integration.nws;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record NwsAlertProperties(
        @JsonProperty("@id") String atId,
        String id,
        String event,
        String headline,
        String severity,
        String certainty,
        String urgency,
        String areaDesc,
        Instant sent,
        Instant effective,
        Instant onset,
        Instant expires,
        String description,
        String instruction,
        String web) {
}
