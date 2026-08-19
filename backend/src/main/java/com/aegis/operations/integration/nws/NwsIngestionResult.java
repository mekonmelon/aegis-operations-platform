package com.aegis.operations.integration.nws;

import java.time.Instant;

public record NwsIngestionResult(
        String source,
        int fetched,
        int created,
        int updated,
        int removed,
        Instant startedAt,
        Instant completedAt,
        String status,
        String error) {
}
