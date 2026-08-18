package com.aegis.operations.controller;

import com.aegis.operations.model.DashboardData;
import com.aegis.operations.service.OperationsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final OperationsService operationsService;

    public DashboardController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @GetMapping
    public DashboardData getDashboard() {
        return operationsService.getDashboard();
    }
}
