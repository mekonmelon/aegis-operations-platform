package com.aegis.operations.controller;

import com.aegis.operations.model.Incident;
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

    public IncidentController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @GetMapping
    public IncidentsResponse listIncidents(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String kind,
            @RequestParam(required = false) String status) {
        return new IncidentsResponse(operationsService.listIncidents(search, severity, kind, status));
    }

    @GetMapping("/{incidentId}")
    public Incident getIncident(@PathVariable String incidentId) {
        return operationsService.getIncident(incidentId);
    }

    public record IncidentsResponse(List<Incident> incidents) {
    }
}
