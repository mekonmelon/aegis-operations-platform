package com.aegis.operations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = ElasticsearchRepositoriesAutoConfiguration.class)
@EnableScheduling
public class AegisBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AegisBackendApplication.class, args);
    }
}
