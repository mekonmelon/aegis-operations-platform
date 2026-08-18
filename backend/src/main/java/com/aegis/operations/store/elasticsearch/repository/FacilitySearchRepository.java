package com.aegis.operations.store.elasticsearch.repository;

import com.aegis.operations.store.elasticsearch.document.FacilityDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FacilitySearchRepository extends ElasticsearchRepository<FacilityDocument, String> {
}
