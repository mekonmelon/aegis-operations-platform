package com.aegis.operations.integration.fema;

import java.time.Instant;
import java.util.List;

public record FemaIngestionStatus(
        String source,
        List<String> states,
        Instant lastAttempt,
        Instant lastSuccessfulSync,
        FemaIngestionResult lastResult,
        String lastError) {
}
