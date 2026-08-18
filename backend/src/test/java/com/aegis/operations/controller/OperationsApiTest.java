package com.aegis.operations.controller;

import com.aegis.operations.store.InMemoryOperationsStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "aegis.storage=memory")
@AutoConfigureMockMvc
class OperationsApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InMemoryOperationsStore store;

    @BeforeEach
    void resetStore() {
        store.resetWithDemoData();
    }

    @Test
    void getDashboardReturnsSynchronizedSnapshot() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastUpdated").value("2026-08-17T14:32:00Z"))
                .andExpect(jsonPath("$.incidents", hasSize(4)))
                .andExpect(jsonPath("$.resources", hasSize(4)))
                .andExpect(jsonPath("$.facilities", hasSize(5)))
                .andExpect(jsonPath("$.recommendations", hasSize(1)))
                .andExpect(jsonPath("$.incidents[0].status").value("escalating"));
    }

    @Test
    void getIncidentReturnsIncidentDetails() throws Exception {
        mockMvc.perform(get("/api/incidents/INC-2048"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INC-2048"))
                .andExpect(jsonPath("$.kind").value("flood"))
                .andExpect(jsonPath("$.severity").value("critical"))
                .andExpect(jsonPath("$.status").value("escalating"))
                .andExpect(jsonPath("$.affectedFacilityIds[0]").value("FAC-2"));
    }

    @Test
    void getUnknownIncidentReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/incidents/INC-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("INCIDENT_NOT_FOUND"));
    }

    @Test
    void incidentTextSearchMatchesTitleLocationAndDescription() throws Exception {
        mockMvc.perform(get("/api/incidents?search=generator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidents", hasSize(1)))
                .andExpect(jsonPath("$.incidents[0].id").value("INC-2046"));
    }

    @Test
    void incidentFiltersCombineWithAnd() throws Exception {
        mockMvc.perform(get("/api/incidents?search=river&severity=critical&kind=flood"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidents", hasSize(1)))
                .andExpect(jsonPath("$.incidents[0].id").value("INC-2048"));
    }

    @Test
    void approveRecommendationUpdatesDashboardState() throws Exception {
        mockMvc.perform(post("/api/recommendations/REC-1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operator\":\"demo-operator\",\"note\":\"Approved from test.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations[0].status").value("approved"))
                .andExpect(jsonPath("$.recommendations[0].statusMessage").value("Deployment approved. Resource assignment is reflected in the incident record."))
                .andExpect(jsonPath("$.incidents[0].status").value("response_active"))
                .andExpect(jsonPath("$.incidents[0].assignedResourceIds", hasItem("RES-1")))
                .andExpect(jsonPath("$.resources[0].available").value(13));
    }

    @Test
    void dismissRecommendationUpdatesDashboardStateWithoutAssigningResource() throws Exception {
        mockMvc.perform(post("/api/recommendations/REC-1/dismiss")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"operator\":\"demo-operator\",\"reason\":\"Deferred.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations[0].status").value("dismissed"))
                .andExpect(jsonPath("$.recommendations[0].statusMessage").value("Recommendation dismissed for this demo session."))
                .andExpect(jsonPath("$.incidents[0].status").value("escalating"))
                .andExpect(jsonPath("$.incidents[0].assignedResourceIds", hasSize(0)))
                .andExpect(jsonPath("$.resources[0].available").value(14));
    }

    @Test
    void secondRecommendationTransitionReturnsConflict() throws Exception {
        mockMvc.perform(post("/api/recommendations/REC-1/approve"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/recommendations/REC-1/dismiss"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("INVALID_RECOMMENDATION_TRANSITION"));
    }
}
