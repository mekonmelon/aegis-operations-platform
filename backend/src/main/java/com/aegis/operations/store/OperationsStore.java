package com.aegis.operations.store;

import com.aegis.operations.model.DashboardData;
import com.aegis.operations.model.Facility;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.model.Resource;
import java.util.List;
import java.util.Optional;

public interface OperationsStore {
    DashboardData dashboardSnapshot();
    List<Incident> searchIncidents(IncidentSearchCriteria criteria);
    Optional<Incident> incidentSnapshot(String incidentId);
    Optional<Resource> resourceSnapshot(String resourceId);
    Optional<Recommendation> recommendationSnapshot(String recommendationId);
    List<Resource> resourceSnapshots();
    List<Facility> facilitySnapshots();
    List<Recommendation> recommendationSnapshots();
    void saveIncident(Incident incident);
    void saveResource(Resource resource);
    void saveRecommendation(Recommendation recommendation);
    void updateLastUpdated();
    void resetWithDemoData();
}
