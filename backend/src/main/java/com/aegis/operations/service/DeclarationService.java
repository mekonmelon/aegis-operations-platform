package com.aegis.operations.service;

import com.aegis.operations.exception.NotFoundException;
import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentDeclarationLink;
import com.aegis.operations.store.DeclarationSearchCriteria;
import com.aegis.operations.store.OperationsStore;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DeclarationService {
    private final OperationsStore store;

    public DeclarationService(OperationsStore store) {
        this.store = store;
    }

    public List<DisasterDeclaration> listDeclarations(String search, String state, String incidentType,
            String declarationType) {
        return store.searchDeclarations(new DeclarationSearchCriteria(search, state, incidentType, declarationType));
    }

    public DisasterDeclaration getDeclaration(String declarationId) {
        return store.declarationSnapshot(declarationId)
                .orElseThrow(() -> new NotFoundException("DECLARATION_NOT_FOUND",
                        "Disaster declaration " + declarationId + " was not found."));
    }

    public List<IncidentDeclarationMatch> relatedDeclarationsForIncident(String incidentId) {
        store.incidentSnapshot(incidentId)
                .orElseThrow(() -> new NotFoundException("INCIDENT_NOT_FOUND",
                        "Incident " + incidentId + " was not found."));

        return store.linksForIncident(incidentId).stream()
                .map(link -> new IncidentDeclarationMatch(link,
                        store.declarationSnapshot(link.getDeclarationId()).orElse(null)))
                .filter(match -> match.declaration() != null)
                .toList();
    }

    public List<DeclarationIncidentMatch> relatedIncidentsForDeclaration(String declarationId) {
        getDeclaration(declarationId);

        return store.linksForDeclaration(declarationId).stream()
                .map(link -> new DeclarationIncidentMatch(link,
                        store.incidentSnapshot(link.getIncidentId()).orElse(null)))
                .filter(match -> match.incident() != null)
                .toList();
    }

    public record IncidentDeclarationMatch(
            String incidentId,
            DisasterDeclaration declaration,
            double confidence,
            List<String> reasons) {
        IncidentDeclarationMatch(IncidentDeclarationLink link, DisasterDeclaration declaration) {
            this(link.getIncidentId(), declaration, link.getConfidence(), link.getReasons());
        }
    }

    public record DeclarationIncidentMatch(
            String declarationId,
            Incident incident,
            double confidence,
            List<String> reasons) {
        DeclarationIncidentMatch(IncidentDeclarationLink link, Incident incident) {
            this(link.getDeclarationId(), incident, link.getConfidence(), link.getReasons());
        }
    }
}
