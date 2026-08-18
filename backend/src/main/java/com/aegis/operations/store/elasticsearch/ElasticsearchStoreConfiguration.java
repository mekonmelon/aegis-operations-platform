package com.aegis.operations.store.elasticsearch;

import com.aegis.operations.store.elasticsearch.repository.IncidentSearchRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@ConditionalOnProperty(name = "aegis.storage", havingValue = "elasticsearch", matchIfMissing = true)
@EnableElasticsearchRepositories(basePackageClasses = IncidentSearchRepository.class)
public class ElasticsearchStoreConfiguration {
}
