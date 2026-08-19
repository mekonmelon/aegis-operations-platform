package com.aegis.operations.controller;

import com.aegis.operations.model.Incident;
import com.aegis.operations.service.DeclarationService;
import com.aegis.operations.service.DeclarationService.IncidentDeclarationMatch;
import com.aegis.operations.service.OperationsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {
    private final OperationsService operationsService;
    private final DeclarationService declarationService;

    public IncidentController(OperationsService operationsService, DeclarationService declarationService) {
        this.operationsService = operationsService;
        this.declarationService = declarationService;
    }

    @GetMapping
    public IncidentsResponse listIncidents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String source) {
        return new IncidentsResponse(operationsService.listIncidents(search, severity, kind, status, source));
    }

    @GetMapping("/{incidentId}")
    public Incident getIncident(@PathVariable String incidentId) {
        return operationsService.getIncident(incidentId);
    }

    @GetMapping("/{incidentId}/declarations")
    public IncidentDeclarationsResponse relatedDeclarations(@PathVariable String incidentId) {
        return new IncidentDeclarationsResponse(declarationService.relatedDeclarationsForIncident(incidentId));
    }

    public record IncidentsResponse(List<Incident> incidents) {
    }

    public record IncidentDeclarationsResponse(List<IncidentDeclarationMatch> declarations) {
    }
}
