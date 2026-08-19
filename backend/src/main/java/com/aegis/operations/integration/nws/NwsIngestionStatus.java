package com.aegis.operations.integration.nws;

import java.time.Instant;
import java.util.List;

public record NwsIngestionStatus(
        String source,
        boolean enabled,
        boolean scheduledEnabled,
        List<String> areas,
        Instant lastAttempt,
        Instant lastSuccessfulSync,
        NwsIngestionResult lastResult,
        String lastError) {
}
