package com.aegis.operations.store.elasticsearch.repository;

import com.aegis.operations.store.elasticsearch.document.DashboardStateDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DashboardStateRepository extends ElasticsearchRepository<DashboardStateDocument, String> {
}
