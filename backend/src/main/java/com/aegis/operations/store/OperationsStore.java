package com.aegis.operations.store;

import com.aegis.operations.model.DashboardData;
import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.model.Facility;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentDeclarationLink;
import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.model.Resource;
import java.util.List;
import java.util.Optional;

public interface OperationsStore {
    DashboardData dashboardSnapshot();
    List<Incident> searchIncidents(IncidentSearchCriteria criteria);
    List<DisasterDeclaration> searchDeclarations(DeclarationSearchCriteria criteria);
    Optional<Incident> incidentSnapshot(String incidentId);
    Optional<DisasterDeclaration> declarationSnapshot(String declarationId);
    Optional<Resource> resourceSnapshot(String resourceId);
    Optional<Recommendation> recommendationSnapshot(String recommendationId);
    List<Resource> resourceSnapshots();
    List<Facility> facilitySnapshots();
    List<Recommendation> recommendationSnapshots();
    List<Incident> incidentSnapshotsBySource(IncidentSource source);
    List<IncidentDeclarationLink> linksForIncident(String incidentId);
    List<IncidentDeclarationLink> linksForDeclaration(String declarationId);
    void saveIncident(Incident incident);
    void saveDeclaration(DisasterDeclaration declaration);
    void saveIncidentDeclarationLink(IncidentDeclarationLink link);
    void saveResource(Resource resource);
    void saveRecommendation(Recommendation recommendation);
    void deleteIncident(String incidentId);
    void deleteLinksForIncident(String incidentId);
    void deleteLinksForDeclaration(String declarationId);
    void updateLastUpdated();
    void resetWithDemoData();
}
