package com.aegis.operations.service;

import com.aegis.operations.exception.BadRequestException;
import com.aegis.operations.exception.InvalidTransitionException;
import com.aegis.operations.exception.NotFoundException;
import com.aegis.operations.model.DashboardData;
import com.aegis.operations.model.Facility;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentKind;
import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.model.IncidentStatus;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.model.RecommendationStatus;
import com.aegis.operations.model.Resource;
import com.aegis.operations.model.Severity;
import com.aegis.operations.store.IncidentSearchCriteria;
import com.aegis.operations.store.OperationsStore;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class OperationsService {
    private final OperationsStore store;

    public OperationsService(OperationsStore store) {
        this.store = store;
    }

    public DashboardData getDashboard() {
        return store.dashboardSnapshot();
    }

    public List<Incident> listIncidents(String search, String severity, String kind, String status, String source) {
        Severity severityFilter = parseOptional(Severity.class, severity, "severity");
        IncidentKind kindFilter = parseOptional(IncidentKind.class, kind, "kind");
        IncidentStatus statusFilter = parseOptional(IncidentStatus.class, status, "status");
        IncidentSource sourceFilter = parseOptional(IncidentSource.class, source, "source");

        return store.searchIncidents(new IncidentSearchCriteria(search, severityFilter, kindFilter, statusFilter,
                sourceFilter));
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

        Incident incident = store.incidentSnapshot(recommendation.getIncidentId())
                .orElseThrow(() -> new NotFoundException("INCIDENT_NOT_FOUND",
                        "Incident " + recommendation.getIncidentId() + " was not found."));
        Resource resource = store.resourceSnapshot(recommendation.getResourceId())
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

        store.saveRecommendation(recommendation);
        store.saveIncident(incident);
        store.saveResource(resource);
        store.updateLastUpdated();
        return store.dashboardSnapshot();
    }

    public DashboardData dismissRecommendation(String recommendationId) {
        Recommendation recommendation = findRecommendation(recommendationId);
        ensurePending(recommendation);

        recommendation.setStatus(RecommendationStatus.DISMISSED);
        recommendation.setStatusMessage("Recommendation dismissed for this demo session.");
        store.saveRecommendation(recommendation);
        store.updateLastUpdated();

        return store.dashboardSnapshot();
    }

    private Recommendation findRecommendation(String recommendationId) {
        return store.recommendationSnapshot(recommendationId)
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
