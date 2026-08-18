package com.aegis.operations.service;

import com.aegis.operations.exception.BadRequestException;
import com.aegis.operations.exception.InvalidTransitionException;
import com.aegis.operations.exception.NotFoundException;
import com.aegis.operations.model.DashboardData;
import com.aegis.operations.model.Facility;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentKind;
import com.aegis.operations.model.IncidentStatus;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.model.RecommendationStatus;
import com.aegis.operations.model.Resource;
import com.aegis.operations.model.Severity;
import com.aegis.operations.store.InMemoryOperationsStore;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OperationsService {
    private final InMemoryOperationsStore store;

    public OperationsService(InMemoryOperationsStore store) {
        this.store = store;
    }

    public DashboardData getDashboard() {
        return store.dashboardSnapshot();
    }

    public List<Incident> listIncidents(String search, String severity, String kind, String status) {
        String normalizedSearch = StringUtils.hasText(search) ? search.trim().toLowerCase() : null;
        Severity severityFilter = parseOptional(Severity.class, severity, "severity");
        IncidentKind kindFilter = parseOptional(IncidentKind.class, kind, "kind");
        IncidentStatus statusFilter = parseOptional(IncidentStatus.class, status, "status");

        return store.incidentSnapshots().stream()
                .filter(incident -> normalizedSearch == null
                        || incident.getTitle().toLowerCase().contains(normalizedSearch)
                        || incident.getLocation().toLowerCase().contains(normalizedSearch))
                .filter(incident -> severityFilter == null || incident.getSeverity() == severityFilter)
                .filter(incident -> kindFilter == null || incident.getKind() == kindFilter)
                .filter(incident -> statusFilter == null || incident.getStatus() == statusFilter)
                .toList();
    }

    public Incident getIncident(String incidentId) {
        return store.incidentSnapshot(incidentId)
                .orElseThrow(() -> new NotFoundException("INCIDENT_NOT_FOUND",
                        "Incident " + incidentId + " was not found."));
    }

    public List<Resource> listResources() {
        return store.resourceSnapshots();
    }

    public List<Facility> listFacilities() {
        return store.facilitySnapshots();
    }

    public List<Recommendation> listRecommendations(String status, String incidentId) {
        RecommendationStatus statusFilter = parseOptional(RecommendationStatus.class, status, "status");

        return store.recommendationSnapshots().stream()
                .filter(recommendation -> statusFilter == null || recommendation.getStatus() == statusFilter)
                .filter(recommendation -> !StringUtils.hasText(incidentId)
                        || recommendation.getIncidentId().equals(incidentId))
                .toList();
    }

    public DashboardData approveRecommendation(String recommendationId) {
        Recommendation recommendation = findRecommendation(recommendationId);
        ensurePending(recommendation);

        Incident incident = store.incidentReference(recommendation.getIncidentId())
                .orElseThrow(() -> new NotFoundException("INCIDENT_NOT_FOUND",
                        "Incident " + recommendation.getIncidentId() + " was not found."));
        Resource resource = store.resourceReference(recommendation.getResourceId())
                .orElseThrow(() -> new NotFoundException("RESOURCE_NOT_FOUND",
                        "Resource " + recommendation.getResourceId() + " was not found."));

        recommendation.setStatus(RecommendationStatus.APPROVED);
        recommendation.setStatusMessage("Deployment approved. Resource assignment is reflected in the incident record.");

        if (resource.getAvailable() > 0) {
            resource.setAvailable(resource.getAvailable() - 1);
        }

        if (!incident.getAssignedResourceIds().contains(resource.getId())) {
            incident.getAssignedResourceIds().add(resource.getId());
        }

        if (incident.getStatus() == IncidentStatus.ESCALATING) {
            incident.setStatus(IncidentStatus.RESPONSE_ACTIVE);
        }

        store.touch();
        return store.dashboardSnapshot();
    }

    public DashboardData dismissRecommendation(String recommendationId) {
        Recommendation recommendation = findRecommendation(recommendationId);
        ensurePending(recommendation);

        recommendation.setStatus(RecommendationStatus.DISMISSED);
        recommendation.setStatusMessage("Recommendation dismissed for this demo session.");
        store.touch();

        return store.dashboardSnapshot();
    }

    private Recommendation findRecommendation(String recommendationId) {
        return store.recommendationReference(recommendationId)
                .orElseThrow(() -> new NotFoundException("RECOMMENDATION_NOT_FOUND",
                        "Recommendation " + recommendationId + " was not found."));
    }

    private void ensurePending(Recommendation recommendation) {
        if (recommendation.getStatus() != RecommendationStatus.PENDING) {
            throw new InvalidTransitionException("INVALID_RECOMMENDATION_TRANSITION",
                    "Recommendation " + recommendation.getId() + " is already "
                            + recommendation.getStatus().jsonValue() + ".");
        }
    }

    private static <T extends Enum<T> & com.aegis.operations.model.JsonEnum> T parseOptional(
            Class<T> enumType, String value, String parameterName) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        for (T candidate : enumType.getEnumConstants()) {
            if (candidate.jsonValue().equals(value)) {
                return candidate;
            }
        }

        throw new BadRequestException("INVALID_QUERY_PARAMETER",
                "Invalid " + parameterName + " value: " + value + ".");
    }
}
