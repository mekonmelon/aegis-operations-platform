package com.aegis.operations.model;

import java.time.Instant;
import java.util.List;

public record DashboardData(
        Instant lastUpdated,
        List<Incident> incidents,
        List<Resource> resources,
        List<Facility> facilities,
        List<Recommendation> recommendations) {
}
