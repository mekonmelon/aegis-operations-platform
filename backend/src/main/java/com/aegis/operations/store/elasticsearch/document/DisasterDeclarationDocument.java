package com.aegis.operations.store.elasticsearch.document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "aegis-disaster-declarations")
public class DisasterDeclarationDocument {
    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private Integer disasterNumber;
    @Field(type = FieldType.Keyword)
    private String declarationType;
    @Field(type = FieldType.Keyword)
    private String state;
    @Field(type = FieldType.Text)
    private String title;
    @Field(type = FieldType.Keyword)
    private String incidentType;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant declarationDate;
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate incidentBeginDate;
    @Field(type = FieldType.Date, format = DateFormat.date)
    private LocalDate incidentEndDate;
    @Field(type = FieldType.Text)
    private List<String> declaredAreas = new ArrayList<>();
    private boolean individualAssistanceDeclared;
    private boolean publicAssistanceDeclared;
    private boolean hazardMitigationDeclared;
    @Field(type = FieldType.Keyword)
    private String source;
    @Field(type = FieldType.Keyword)
    private String sourceId;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant sourceUpdatedAt;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant ingestedAt;

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
