package com.aegis.operations.model;

import java.util.ArrayList;
import java.util.List;

public class IncidentDeclarationLink {
    private String id;
    private String incidentId;
    private String declarationId;
    private double confidence;
    private List<String> reasons = new ArrayList<>();

    public IncidentDeclarationLink() {
    }

    public IncidentDeclarationLink(String incidentId, String declarationId, double confidence, List<String> reasons) {
        this.id = incidentId + "::" + declarationId;
        this.incidentId = incidentId;
        this.declarationId = declarationId;
        this.confidence = confidence;
        this.reasons = new ArrayList<>(reasons);
    }

    public IncidentDeclarationLink copy() {
        return new IncidentDeclarationLink(incidentId, declarationId, confidence, reasons);
    }

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
