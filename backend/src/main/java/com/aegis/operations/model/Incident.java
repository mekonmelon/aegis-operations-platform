package com.aegis.operations.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Incident {
    private String id;
    private String title;
    private IncidentKind kind;
    private Severity severity;
    private String location;
    private String state;
    private IncidentStatus status;
    private Instant reportedAt;
    private RegionCoordinates coordinates;
    private String description;
    private List<String> affectedFacilityIds = new ArrayList<>();
    private List<String> assignedResourceIds = new ArrayList<>();
    private IncidentSource source = IncidentSource.DEMO;
    private String sourceId;
    private String sourceUrl;
    private Instant sourceUpdatedAt;
    private Instant ingestedAt;

    public Incident() {
    }

    public Incident(String id, String title, IncidentKind kind, Severity severity, String location,
            IncidentStatus status, Instant reportedAt, RegionCoordinates coordinates, String description,
            List<String> affectedFacilityIds, List<String> assignedResourceIds) {
        this(id, title, kind, severity, location, null, status, reportedAt, coordinates, description,
                affectedFacilityIds, assignedResourceIds, IncidentSource.DEMO, id, null, reportedAt, reportedAt);
    }

    public Incident(String id, String title, IncidentKind kind, Severity severity, String location,
            IncidentStatus status, Instant reportedAt, RegionCoordinates coordinates, String description,
            List<String> affectedFacilityIds, List<String> assignedResourceIds, IncidentSource source, String sourceId,
            String sourceUrl, Instant sourceUpdatedAt, Instant ingestedAt) {
        this(id, title, kind, severity, location, null, status, reportedAt, coordinates, description,
                affectedFacilityIds, assignedResourceIds, source, sourceId, sourceUrl, sourceUpdatedAt, ingestedAt);
    }

    public Incident(String id, String title, IncidentKind kind, Severity severity, String location, String state,
            IncidentStatus status, Instant reportedAt, RegionCoordinates coordinates, String description,
            List<String> affectedFacilityIds, List<String> assignedResourceIds, IncidentSource source, String sourceId,
            String sourceUrl, Instant sourceUpdatedAt, Instant ingestedAt) {
        this.id = id;
        this.title = title;
        this.kind = kind;
        this.severity = severity;
        this.location = location;
        this.state = state;
        this.status = status;
        this.reportedAt = reportedAt;
        this.coordinates = coordinates;
        this.description = description;
        this.affectedFacilityIds = new ArrayList<>(affectedFacilityIds);
        this.assignedResourceIds = new ArrayList<>(assignedResourceIds);
        this.source = source;
        this.sourceId = sourceId;
        this.sourceUrl = sourceUrl;
        this.sourceUpdatedAt = sourceUpdatedAt;
        this.ingestedAt = ingestedAt;
    }

    public Incident copy() {
        return new Incident(id, title, kind, severity, location, state, status, reportedAt, coordinates, description,
                affectedFacilityIds, assignedResourceIds, source, sourceId, sourceUrl, sourceUpdatedAt, ingestedAt);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public IncidentKind getKind() { return kind; }
    public void setKind(IncidentKind kind) { this.kind = kind; }
    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public IncidentStatus getStatus() { return status; }
    public void setStatus(IncidentStatus status) { this.status = status; }
    public Instant getReportedAt() { return reportedAt; }
    public void setReportedAt(Instant reportedAt) { this.reportedAt = reportedAt; }
    public RegionCoordinates getCoordinates() { return coordinates; }
    public void setCoordinates(RegionCoordinates coordinates) { this.coordinates = coordinates; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getAffectedFacilityIds() { return affectedFacilityIds; }
    public void setAffectedFacilityIds(List<String> affectedFacilityIds) { this.affectedFacilityIds = new ArrayList<>(affectedFacilityIds); }
    public List<String> getAssignedResourceIds() { return assignedResourceIds; }
    public void setAssignedResourceIds(List<String> assignedResourceIds) { this.assignedResourceIds = new ArrayList<>(assignedResourceIds); }
    public IncidentSource getSource() { return source; }
    public void setSource(IncidentSource source) { this.source = source; }
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public Instant getSourceUpdatedAt() { return sourceUpdatedAt; }
    public void setSourceUpdatedAt(Instant sourceUpdatedAt) { this.sourceUpdatedAt = sourceUpdatedAt; }
    public Instant getIngestedAt() { return ingestedAt; }
    public void setIngestedAt(Instant ingestedAt) { this.ingestedAt = ingestedAt; }
}
