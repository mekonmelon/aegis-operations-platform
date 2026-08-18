package com.aegis.operations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;

@SpringBootApplication(exclude = ElasticsearchRepositoriesAutoConfiguration.class)
public class AegisBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AegisBackendApplication.class, args);
    }
}
