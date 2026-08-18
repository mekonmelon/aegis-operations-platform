package com.aegis.operations.store.elasticsearch.repository;

import com.aegis.operations.store.elasticsearch.document.RecommendationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface RecommendationSearchRepository extends ElasticsearchRepository<RecommendationDocument, String> {
}
