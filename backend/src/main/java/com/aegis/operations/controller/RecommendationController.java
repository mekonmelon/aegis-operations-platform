package com.aegis.operations.controller;

import com.aegis.operations.model.DashboardData;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.service.OperationsService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {
    private final OperationsService operationsService;

    public RecommendationController(OperationsService operationsService) {
        this.operationsService = operationsService;
    }

    @GetMapping
    public RecommendationsResponse listRecommendations(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String incidentId) {
        return new RecommendationsResponse(operationsService.listRecommendations(status, incidentId));
    }

    @PostMapping("/{recommendationId}/approve")
    public DashboardData approveRecommendation(
            @PathVariable String recommendationId,
            @RequestBody(required = false) RecommendationActionRequest request) {
        return operationsService.approveRecommendation(recommendationId);
    }

    @PostMapping("/{recommendationId}/dismiss")
    public DashboardData dismissRecommendation(
            @PathVariable String recommendationId,
            @RequestBody(required = false) RecommendationActionRequest request) {
        return operationsService.dismissRecommendation(recommendationId);
    }

    public record RecommendationsResponse(List<Recommendation> recommendations) {
    }

    public record RecommendationActionRequest(String operator, String note, String reason) {
    }
}
