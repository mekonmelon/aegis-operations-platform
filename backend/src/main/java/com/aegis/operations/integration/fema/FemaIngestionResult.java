package com.aegis.operations.integration.fema;

import java.time.Instant;

public record FemaIngestionResult(
        String source,
        int fetchedRecords,
        int declarations,
        int created,
        int updated,
        int linksCreated,
        Instant startedAt,
        Instant completedAt,
        String status,
        String error) {
}
