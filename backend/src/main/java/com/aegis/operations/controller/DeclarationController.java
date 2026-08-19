package com.aegis.operations.controller;

import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.service.DeclarationService;
import com.aegis.operations.service.DeclarationService.DeclarationIncidentMatch;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/declarations")
public class DeclarationController {
    private final DeclarationService declarationService;

    public DeclarationController(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    @GetMapping
    public DeclarationsResponse listDeclarations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String incidentType,
            @RequestParam(required = false) String declarationType) {
        return new DeclarationsResponse(declarationService.listDeclarations(search, state, incidentType,
                declarationType));
    }

    @GetMapping("/{declarationId}")
    public DisasterDeclaration getDeclaration(@PathVariable String declarationId) {
        return declarationService.getDeclaration(declarationId);
    }

    @GetMapping("/{declarationId}/incidents")
    public DeclarationIncidentsResponse relatedIncidents(@PathVariable String declarationId) {
        return new DeclarationIncidentsResponse(declarationService.relatedIncidentsForDeclaration(declarationId));
    }

    public record DeclarationsResponse(List<DisasterDeclaration> declarations) {
    }

    public record DeclarationIncidentsResponse(List<DeclarationIncidentMatch> incidents) {
    }
}
