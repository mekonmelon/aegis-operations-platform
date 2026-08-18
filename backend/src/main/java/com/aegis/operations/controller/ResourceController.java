package com.aegis.operations.controller;

import com.aegis.operations.model.Resource;
import com.aegis.operations.service.OperationsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {
    private final OperationsService operationsService;

    public ResourceController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @GetMapping
    public ResourcesResponse listResources() {
        return new ResourcesResponse(operationsService.listResources());
    }

    public record ResourcesResponse(List<Resource> resources) {
    }
}
