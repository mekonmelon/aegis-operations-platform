package com.aegis.operations.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DisasterDeclaration {
    private String id;
    private Integer disasterNumber;
    private String declarationType;
    private String state;
    private String title;
    private String incidentType;
    private Instant declarationDate;
    private LocalDate incidentBeginDate;
    private LocalDate incidentEndDate;
    private List<String> declaredAreas = new ArrayList<>();
    private boolean individualAssistanceDeclared;
    private boolean publicAssistanceDeclared;
    private boolean hazardMitigationDeclared;
    private String source = "fema";
    private String sourceId;
    private Instant sourceUpdatedAt;
    private Instant ingestedAt;

    public DisasterDeclaration() {
    }

    public DisasterDeclaration(String id, Integer disasterNumber, String declarationType, String state, String title,
            String incidentType, Instant declarationDate, LocalDate incidentBeginDate, LocalDate incidentEndDate,
            List<String> declaredAreas, boolean individualAssistanceDeclared, boolean publicAssistanceDeclared,
            boolean hazardMitigationDeclared, String source, String sourceId, Instant sourceUpdatedAt,
            Instant ingestedAt) {
        this.id = id;
        this.disasterNumber = disasterNumber;
        this.declarationType = declarationType;
        this.state = state;
        this.title = title;
        this.incidentType = incidentType;
        this.declarationDate = declarationDate;
        this.incidentBeginDate = incidentBeginDate;
        this.incidentEndDate = incidentEndDate;
        this.declaredAreas = new ArrayList<>(declaredAreas);
        this.individualAssistanceDeclared = individualAssistanceDeclared;
        this.publicAssistanceDeclared = publicAssistanceDeclared;
        this.hazardMitigationDeclared = hazardMitigationDeclared;
        this.source = source;
        this.sourceId = sourceId;
        this.sourceUpdatedAt = sourceUpdatedAt;
        this.ingestedAt = ingestedAt;
    }

    public DisasterDeclaration copy() {
        return new DisasterDeclaration(id, disasterNumber, declarationType, state, title, incidentType,
                declarationDate, incidentBeginDate, incidentEndDate, declaredAreas, individualAssistanceDeclared,
                publicAssistanceDeclared, hazardMitigationDeclared, source, sourceId, sourceUpdatedAt, ingestedAt);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Integer getDisasterNumber() { return disasterNumber; }
    public void setDisasterNumber(Integer disasterNumber) { this.disasterNumber = disasterNumber; }
    public String getDeclarationType() { return declarationType; }
    public void setDeclarationType(String declarationType) { this.declarationType = declarationType; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getIncidentType() { return incidentType; }
    public void setIncidentType(String incidentType) { this.incidentType = incidentType; }
    public Instant getDeclarationDate() { return declarationDate; }
    public void setDeclarationDate(Instant declarationDate) { this.declarationDate = declarationDate; }
    public LocalDate getIncidentBeginDate() { return incidentBeginDate; }
    public void setIncidentBeginDate(LocalDate incidentBeginDate) { this.incidentBeginDate = incidentBeginDate; }
    public LocalDate getIncidentEndDate() { return incidentEndDate; }
    public void setIncidentEndDate(LocalDate incidentEndDate) { this.incidentEndDate = incidentEndDate; }
    public List<String> getDeclaredAreas() { return declaredAreas; }
    public void setDeclaredAreas(List<String> declaredAreas) { this.declaredAreas = new ArrayList<>(declaredAreas); }
    public boolean isIndividualAssistanceDeclared() { return individualAssistanceDeclared; }
    public void setIndividualAssistanceDeclared(boolean individualAssistanceDeclared) { this.individualAssistanceDeclared = individualAssistanceDeclared; }
    public boolean isPublicAssistanceDeclared() { return publicAssistanceDeclared; }
    public void setPublicAssistanceDeclared(boolean publicAssistanceDeclared) { this.publicAssistanceDeclared = publicAssistanceDeclared; }
    public boolean isHazardMitigationDeclared() { return hazardMitigationDeclared; }
    public void setHazardMitigationDeclared(boolean hazardMitigationDeclared) { this.hazardMitigationDeclared = hazardMitigationDeclared; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    public Instant getSourceUpdatedAt() { return sourceUpdatedAt; }
    public void setSourceUpdatedAt(Instant sourceUpdatedAt) { this.sourceUpdatedAt = sourceUpdatedAt; }
    public Instant getIngestedAt() { return ingestedAt; }
    public void setIngestedAt(Instant ingestedAt) { this.ingestedAt = ingestedAt; }
}
