package com.aegis.operations.store;

import com.aegis.operations.model.DashboardData;
import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.model.Facility;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentDeclarationLink;
import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.model.Resource;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConditionalOnProperty(name = "aegis.storage", havingValue = "memory")
public class InMemoryOperationsStore implements OperationsStore {
    private Instant lastUpdated;
    private final Map<String, Incident> incidents = new LinkedHashMap<>();
    private final Map<String, Resource> resources = new LinkedHashMap<>();
    private final Map<String, Facility> facilities = new LinkedHashMap<>();
    private final Map<String, Recommendation> recommendations = new LinkedHashMap<>();
    private final Map<String, DisasterDeclaration> declarations = new LinkedHashMap<>();
    private final Map<String, IncidentDeclarationLink> links = new LinkedHashMap<>();

    public InMemoryOperationsStore() {
        resetWithDemoData();
    }

    @Override
    public synchronized void resetWithDemoData() {
        lastUpdated = DemoOperationsData.INITIAL_LAST_UPDATED;
        incidents.clear();
        resources.clear();
        facilities.clear();
        recommendations.clear();
        declarations.clear();
        links.clear();

        DemoOperationsData.incidents().forEach(this::putIncident);
        DemoOperationsData.resources().forEach(this::putResource);
        DemoOperationsData.facilities().forEach(this::putFacility);
        DemoOperationsData.recommendations().forEach(this::putRecommendation);
    }

    @Override
    public synchronized DashboardData dashboardSnapshot() {
        return new DashboardData(lastUpdated, incidentSnapshots(), resourceSnapshots(), facilitySnapshots(),
                recommendationSnapshots());
    }

    @Override
    public synchronized List<Incident> searchIncidents(IncidentSearchCriteria criteria) {
        String normalizedSearch = StringUtils.hasText(criteria.search()) ? criteria.search().trim().toLowerCase() : null;

        return incidentSnapshots().stream()
                .filter(incident -> normalizedSearch == null
                        || incident.getTitle().toLowerCase().contains(normalizedSearch)
                        || incident.getLocation().toLowerCase().contains(normalizedSearch)
                        || incident.getDescription().toLowerCase().contains(normalizedSearch))
                .filter(incident -> criteria.severity() == null || incident.getSeverity() == criteria.severity())
                .filter(incident -> criteria.kind() == null || incident.getKind() == criteria.kind())
                .filter(incident -> criteria.status() == null || incident.getStatus() == criteria.status())
                .filter(incident -> criteria.source() == null || incident.getSource() == criteria.source())
                .toList();
    }

    @Override
    public synchronized List<DisasterDeclaration> searchDeclarations(DeclarationSearchCriteria criteria) {
        String normalizedSearch = StringUtils.hasText(criteria.search()) ? criteria.search().trim().toLowerCase() : null;
        return declarations.values().stream()
                .map(DisasterDeclaration::copy)
                .filter(declaration -> normalizedSearch == null
                        || declaration.getTitle().toLowerCase().contains(normalizedSearch)
                        || declaration.getIncidentType().toLowerCase().contains(normalizedSearch)
                        || declaration.getDeclaredAreas().stream().anyMatch(area -> area.toLowerCase().contains(normalizedSearch)))
                .filter(declaration -> !StringUtils.hasText(criteria.state())
                        || declaration.getState().equalsIgnoreCase(criteria.state()))
                .filter(declaration -> !StringUtils.hasText(criteria.incidentType())
                        || declaration.getIncidentType().equalsIgnoreCase(criteria.incidentType()))
                .filter(declaration -> !StringUtils.hasText(criteria.declarationType())
                        || declaration.getDeclarationType().equalsIgnoreCase(criteria.declarationType()))
                .toList();
    }

    @Override
    public synchronized Optional<Incident> incidentSnapshot(String incidentId) {
        return Optional.ofNullable(incidents.get(incidentId)).map(Incident::copy);
    }

    @Override
    public synchronized Optional<DisasterDeclaration> declarationSnapshot(String declarationId) {
        return Optional.ofNullable(declarations.get(declarationId)).map(DisasterDeclaration::copy);
    }

    @Override
    public synchronized Optional<Resource> resourceSnapshot(String resourceId) {
        return Optional.ofNullable(resources.get(resourceId)).map(Resource::copy);
    }

    @Override
    public synchronized Optional<Recommendation> recommendationSnapshot(String recommendationId) {
        return Optional.ofNullable(recommendations.get(recommendationId)).map(Recommendation::copy);
    }

    @Override
    public synchronized List<Resource> resourceSnapshots() {
        return resources.values().stream().map(Resource::copy).toList();
    }

    @Override
    public synchronized List<Facility> facilitySnapshots() {
        return facilities.values().stream().map(Facility::copy).toList();
    }

    @Override
    public synchronized List<Recommendation> recommendationSnapshots() {
        return recommendations.values().stream().map(Recommendation::copy).toList();
    }

    public synchronized List<Incident> incidentSnapshots() {
        return incidents.values().stream().map(Incident::copy).toList();
    }

    @Override
    public synchronized List<Incident> incidentSnapshotsBySource(IncidentSource source) {
        return incidents.values().stream()
                .filter(incident -> incident.getSource() == source)
                .map(Incident::copy)
                .toList();
    }

    @Override
    public synchronized List<IncidentDeclarationLink> linksForIncident(String incidentId) {
        return links.values().stream()
                .filter(link -> link.getIncidentId().equals(incidentId))
                .map(IncidentDeclarationLink::copy)
                .toList();
    }

    @Override
    public synchronized List<IncidentDeclarationLink> linksForDeclaration(String declarationId) {
        return links.values().stream()
                .filter(link -> link.getDeclarationId().equals(declarationId))
                .map(IncidentDeclarationLink::copy)
                .toList();
    }

    @Override
    public synchronized void saveIncident(Incident incident) {
        incidents.put(incident.getId(), incident.copy());
    }

    @Override
    public synchronized void saveDeclaration(DisasterDeclaration declaration) {
        declarations.put(declaration.getId(), declaration.copy());
    }

    @Override
    public synchronized void saveIncidentDeclarationLink(IncidentDeclarationLink link) {
        links.put(link.getId(), link.copy());
    }

    @Override
    public synchronized void saveResource(Resource resource) {
        resources.put(resource.getId(), resource.copy());
    }

    @Override
    public synchronized void saveRecommendation(Recommendation recommendation) {
        recommendations.put(recommendation.getId(), recommendation.copy());
    }

    @Override
    public synchronized void deleteIncident(String incidentId) {
        incidents.remove(incidentId);
        deleteLinksForIncident(incidentId);
    }

    @Override
    public synchronized void deleteLinksForIncident(String incidentId) {
        links.values().removeIf(link -> link.getIncidentId().equals(incidentId));
    }

    @Override
    public synchronized void deleteLinksForDeclaration(String declarationId) {
        links.values().removeIf(link -> link.getDeclarationId().equals(declarationId));
    }

    @Override
    public synchronized void updateLastUpdated() {
        lastUpdated = Instant.now();
    }

    private void putIncident(Incident incident) {
        incidents.put(incident.getId(), incident.copy());
    }

    private void putResource(Resource resource) {
        resources.put(resource.getId(), resource.copy());
    }

    private void putFacility(Facility facility) {
        facilities.put(facility.getId(), facility.copy());
    }

    private void putRecommendation(Recommendation recommendation) {
        recommendations.put(recommendation.getId(), recommendation.copy());
    }
}
