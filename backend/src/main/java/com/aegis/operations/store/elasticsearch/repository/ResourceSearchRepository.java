package com.aegis.operations.store.elasticsearch.repository;

import com.aegis.operations.store.elasticsearch.document.ResourceDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ResourceSearchRepository extends ElasticsearchRepository<ResourceDocument, String> {
}
