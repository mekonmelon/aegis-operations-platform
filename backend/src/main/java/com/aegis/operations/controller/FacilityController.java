package com.aegis.operations.controller;

import com.aegis.operations.model.Facility;
import com.aegis.operations.service.OperationsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facilities")
public class FacilityController {
    private final OperationsService operationsService;

    public FacilityController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @GetMapping
    public FacilitiesResponse listFacilities() {
        return new FacilitiesResponse(operationsService.listFacilities());
    }

    public record FacilitiesResponse(List<Facility> facilities) {
    }
}
