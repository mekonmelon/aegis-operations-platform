package com.aegis.operations.store.elasticsearch.document;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "aegis-incident-declaration-links")
public class IncidentDeclarationLinkDocument {
    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String incidentId;
    @Field(type = FieldType.Keyword)
    private String declarationId;
    private double confidence;
    @Field(type = FieldType.Keyword)
    private List<String> reasons = new ArrayList<>();

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }
    public String getDeclarationId() { return declarationId; }
    public void setDeclarationId(String declarationId) { this.declarationId = declarationId; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = new ArrayList<>(reasons); }
}
