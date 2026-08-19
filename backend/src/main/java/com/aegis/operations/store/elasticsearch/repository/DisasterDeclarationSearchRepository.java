package com.aegis.operations.store.elasticsearch.repository;

import com.aegis.operations.store.elasticsearch.document.DisasterDeclarationDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DisasterDeclarationSearchRepository extends ElasticsearchRepository<DisasterDeclarationDocument, String> {
}
