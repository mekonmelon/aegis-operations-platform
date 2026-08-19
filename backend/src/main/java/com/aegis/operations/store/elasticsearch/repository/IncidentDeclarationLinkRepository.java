package com.aegis.operations.store.elasticsearch.repository;

import com.aegis.operations.store.elasticsearch.document.IncidentDeclarationLinkDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IncidentDeclarationLinkRepository extends ElasticsearchRepository<IncidentDeclarationLinkDocument, String> {
    List<IncidentDeclarationLinkDocument> findByIncidentId(String incidentId);
    List<IncidentDeclarationLinkDocument> findByDeclarationId(String declarationId);
    void deleteByIncidentId(String incidentId);
    void deleteByDeclarationId(String declarationId);
}
