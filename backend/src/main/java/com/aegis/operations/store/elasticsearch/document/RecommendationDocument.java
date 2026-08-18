package com.aegis.operations.store.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "aegis-recommendations")
public class RecommendationDocument {
    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String priority;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Text)
    private String detail;
    @Field(type = FieldType.Text)
    private String actionLabel;
    @Field(type = FieldType.Keyword)
    private String incidentId;
    @Field(type = FieldType.Keyword)
    private String resourceId;
    @Field(type = FieldType.Keyword)
    private String status;
    @Field(type = FieldType.Text)
    private String statusMessage;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getActionLabel() { return actionLabel; }
    public void setActionLabel(String actionLabel) { this.actionLabel = actionLabel; }
    public String getIncidentId() { return incidentId; }
    public void setIncidentId(String incidentId) { this.incidentId = incidentId; }
    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
}
