package com.aegis.operations.model;

public class Recommendation {
    private String id;
    private Severity priority;
    private String title;
    private String detail;
    private String actionLabel;
    private String incidentId;
    private String resourceId;
    private RecommendationStatus status;
    private String statusMessage;

    public Recommendation() {
    }

    public Recommendation(String id, Severity priority, String title, String detail, String actionLabel,
            String incidentId, String resourceId, RecommendationStatus status, String statusMessage) {
        this.id = id;
        this.priority = priority;
        this.title = title;
        this.detail = detail;
        this.actionLabel = actionLabel;
        this.incidentId = incidentId;
        this.resourceId = resourceId;
        this.status = status;
        this.statusMessage = statusMessage;
    }

    public Recommendation copy() {
        return new Recommendation(id, priority, title, detail, actionLabel, incidentId, resourceId, status,
                statusMessage);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Severity getPriority() { return priority; }
    public void setPriority(Severity priority) { this.priority = priority; }
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
    public RecommendationStatus getStatus() { return status; }
    public void setStatus(RecommendationStatus status) { this.status = status; }
    public String getStatusMessage() { return statusMessage; }
    public void setStatusMessage(String statusMessage) { this.statusMessage = statusMessage; }
}
