package com.aegis.operations.store.elasticsearch.repository;

import com.aegis.operations.store.elasticsearch.document.IncidentDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IncidentSearchRepository extends ElasticsearchRepository<IncidentDocument, String> {
}
