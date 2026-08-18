package com.aegis.operations.store;

import com.aegis.operations.model.DashboardData;
import com.aegis.operations.model.Facility;
import com.aegis.operations.model.FacilityKind;
import com.aegis.operations.model.FacilityStatus;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentKind;
import com.aegis.operations.model.IncidentStatus;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.model.RecommendationStatus;
import com.aegis.operations.model.RegionCoordinates;
import com.aegis.operations.model.Resource;
import com.aegis.operations.model.ResourceKind;
import com.aegis.operations.model.Severity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class InMemoryOperationsStore {
    private Instant lastUpdated;
    private final Map<String, Incident> incidents = new LinkedHashMap<>();
    private final Map<String, Resource> resources = new LinkedHashMap<>();
    private final Map<String, Facility> facilities = new LinkedHashMap<>();
    private final Map<String, Recommendation> recommendations = new LinkedHashMap<>();

    public InMemoryOperationsStore() {
        reset();
    }

    public synchronized void reset() {
        lastUpdated = Instant.parse("2026-08-17T14:32:00Z");
        incidents.clear();
        resources.clear();
        facilities.clear();
        recommendations.clear();

        putIncident(new Incident("INC-2048", "River District Flooding", IncidentKind.FLOOD, Severity.CRITICAL,
                "North River District", IncidentStatus.ESCALATING, Instant.parse("2026-08-17T14:18:00Z"),
                new RegionCoordinates(31, 30),
                "Rapid river rise is flooding low-lying residential blocks and threatening access to the north evacuation route.",
                List.of("FAC-2"), List.of()));
        putIncident(new Incident("INC-2047", "Ridge Wildfire", IncidentKind.WILDFIRE, Severity.HIGH,
                "West Ridge Preserve", IncidentStatus.RESPONSE_ACTIVE, Instant.parse("2026-08-17T13:46:00Z"),
                new RegionCoordinates(18, 66),
                "Wind-driven fire is moving along the preserve boundary with smoke exposure reported near two neighborhoods.",
                List.of("FAC-5"), List.of("RES-2")));
        putIncident(new Incident("INC-2046", "Grid Power Outage", IncidentKind.OUTAGE, Severity.MODERATE,
                "Eastgate Sector 4", IncidentStatus.MONITORING, Instant.parse("2026-08-17T12:57:00Z"),
                new RegionCoordinates(76, 41),
                "Substation fault has interrupted commercial power across Eastgate with backup generators holding at critical facilities.",
                List.of("FAC-4"), List.of()));
        putIncident(new Incident("INC-2043", "Highway 8 Closure", IncidentKind.ROAD, Severity.LOW,
                "South Junction", IncidentStatus.CONTAINED, Instant.parse("2026-08-17T11:21:00Z"),
                new RegionCoordinates(57, 77),
                "Flood debris and a disabled utility vehicle have closed two lanes while public works clears the corridor.",
                List.of(), List.of()));

        putResource(new Resource("RES-1", ResourceKind.TEAMS, "Response Teams", 14, 20, "teams"));
        putResource(new Resource("RES-2", ResourceKind.VEHICLES, "Response Vehicles", 31, 42, "units"));
        putResource(new Resource("RES-3", ResourceKind.MEDICAL, "Medical Capacity", 186, 240, "beds"));
        putResource(new Resource("RES-4", ResourceKind.SUPPLIES, "Emergency Supplies", 78, 100, "%"));

        putFacility(new Facility("FAC-1", "Mercy General", FacilityKind.HOSPITAL, FacilityStatus.OPERATIONAL,
                new RegionCoordinates(62, 25)));
        putFacility(new Facility("FAC-2", "Northside Clinic", FacilityKind.HOSPITAL, FacilityStatus.AT_RISK,
                new RegionCoordinates(42, 36)));
        putFacility(new Facility("FAC-3", "Central Civic Shelter", FacilityKind.SHELTER, FacilityStatus.OPERATIONAL,
                new RegionCoordinates(49, 56)));
        putFacility(new Facility("FAC-4", "East High Shelter", FacilityKind.SHELTER, FacilityStatus.OPERATIONAL,
                new RegionCoordinates(81, 59)));
        putFacility(new Facility("FAC-5", "Logistics Depot 3", FacilityKind.DEPOT, FacilityStatus.AT_RISK,
                new RegionCoordinates(27, 75)));

        putRecommendation(new Recommendation("REC-1", Severity.CRITICAL, "Deploy Team Alpha-3",
                "Deploy swift-water rescue team Alpha-3 to North River District. Rising water levels may isolate 340 residents within 45 minutes.",
                "Review deployment", "INC-2048", "RES-1", RecommendationStatus.PENDING, null));
    }

    public synchronized DashboardData dashboardSnapshot() {
        return new DashboardData(lastUpdated, incidentSnapshots(), resourceSnapshots(), facilitySnapshots(),
                recommendationSnapshots());
    }

    public synchronized List<Incident> incidentSnapshots() {
        return incidents.values().stream().map(Incident::copy).toList();
    }

    public synchronized Optional<Incident> incidentSnapshot(String incidentId) {
        return Optional.ofNullable(incidents.get(incidentId)).map(Incident::copy);
    }

    public synchronized Optional<Incident> incidentReference(String incidentId) {
        return Optional.ofNullable(incidents.get(incidentId));
    }

    public synchronized List<Resource> resourceSnapshots() {
        return resources.values().stream().map(Resource::copy).toList();
    }

    public synchronized Optional<Resource> resourceReference(String resourceId) {
        return Optional.ofNullable(resources.get(resourceId));
    }

    public synchronized List<Facility> facilitySnapshots() {
        return facilities.values().stream().map(Facility::copy).toList();
    }

    public synchronized List<Recommendation> recommendationSnapshots() {
        return recommendations.values().stream().map(Recommendation::copy).toList();
    }

    public synchronized Optional<Recommendation> recommendationReference(String recommendationId) {
        return Optional.ofNullable(recommendations.get(recommendationId));
    }

    public synchronized void touch() {
        lastUpdated = Instant.now();
    }

    private void putIncident(Incident incident) {
        incidents.put(incident.getId(), incident);
    }

    private void putResource(Resource resource) {
        resources.put(resource.getId(), resource);
    }

    private void putFacility(Facility facility) {
        facilities.put(facility.getId(), facility);
    }

    private void putRecommendation(Recommendation recommendation) {
        recommendations.put(recommendation.getId(), recommendation);
    }
}
