package com.aegis.operations.store.elasticsearch.document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "aegis-incidents")
public class IncidentDocument {
    @Id
    private String id;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Keyword)
    private String kind;
    @Field(type = FieldType.Keyword)
    private String severity;
    @Field(type = FieldType.Text)
    private String location;
    @Field(type = FieldType.Keyword)
    private String state;
    @Field(type = FieldType.Keyword)
    private String status;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant reportedAt;
    @Field(type = FieldType.Object)
    private RegionCoordinatesDocument coordinates;
    @Field(type = FieldType.Text)
    private String description;
    @Field(type = FieldType.Keyword)
    private List<String> affectedFacilityIds = new ArrayList<>();
    @Field(type = FieldType.Keyword)
    private List<String> assignedResourceIds = new ArrayList<>();
    @Field(type = FieldType.Keyword)
    private String source;
    @Field(type = FieldType.Keyword)
    private String sourceId;
    @Field(type = FieldType.Keyword)
    private String sourceUrl;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant sourceUpdatedAt;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant ingestedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getReportedAt() { return reportedAt; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }
    public RegionCoordinatesDocument getCoordinates() { return coordinates; }
    public void setCoordinates(RegionCoordinatesDocument coordinates) { this.coordinates = coordinates; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getAffectedFacilityIds() { return affectedFacilityIds; }
    public void setAffectedFacilityIds(List<String> affectedFacilityIds) { this.affectedFacilityIds = new ArrayList<>(affectedFacilityIds); }
    public List<String> getAssignedResourceIds() { return assignedResourceIds; }
    public void setAssignedResourceIds(List<String> assignedResourceIds) { this.assignedResourceIds = new ArrayList<>(assignedResourceIds); }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public Instant getSourceUpdatedAt() { return sourceUpdatedAt; }
    public void setSourceUpdatedAt(Instant sourceUpdatedAt) { this.sourceUpdatedAt = sourceUpdatedAt; }
    public Instant getIngestedAt() { return ingestedAt; }
    public void setIngestedAt(Instant ingestedAt) { this.ingestedAt = ingestedAt; }
}
