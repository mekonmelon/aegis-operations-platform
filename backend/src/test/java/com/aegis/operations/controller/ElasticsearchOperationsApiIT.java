package com.aegis.operations.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "aegis.storage=elasticsearch")
@AutoConfigureMockMvc
class ElasticsearchOperationsApiIT {
    @Container
    static ElasticsearchContainer elasticsearch = new ElasticsearchContainer(
            DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:8.15.5"))
            .withEnv("xpack.security.enabled", "false");

    @Autowired
    private MockMvc mockMvc;

    @DynamicPropertySource
    static void elasticsearchProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearch::getHttpHostAddress);
    }

    @Test
    void seedsEmptyElasticsearchAndLoadsDashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidents", hasSize(4)))
                .andExpect(jsonPath("$.resources", hasSize(4)))
                .andExpect(jsonPath("$.facilities", hasSize(5)))
                .andExpect(jsonPath("$.recommendations", hasSize(1)));
    }

    @Test
    void searchesIncidentsWithTextAndFiltersInElasticsearch() throws Exception {
        mockMvc.perform(get("/api/incidents?search=river"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidents[*].id", hasItem("INC-2048")));

        mockMvc.perform(get("/api/incidents?severity=critical"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidents", hasSize(1)))
                .andExpect(jsonPath("$.incidents[0].id").value("INC-2048"));

        mockMvc.perform(get("/api/incidents?search=river&severity=critical&kind=flood"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidents", hasSize(1)))
                .andExpect(jsonPath("$.incidents[0].id").value("INC-2048"));
    }

    @Test
    void recommendationApprovalPersistsToElasticsearchDocuments() throws Exception {
        mockMvc.perform(post("/api/recommendations/REC-1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations[0].status").value("approved"))
                .andExpect(jsonPath("$.incidents[0].assignedResourceIds", hasItem("RES-1")))
                .andExpect(jsonPath("$.resources[0].available").value(13));

        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendations[0].status").value("approved"))
                .andExpect(jsonPath("$.incidents[0].assignedResourceIds", hasItem("RES-1")))
                .andExpect(jsonPath("$.resources[0].available").value(13));
    }
}
