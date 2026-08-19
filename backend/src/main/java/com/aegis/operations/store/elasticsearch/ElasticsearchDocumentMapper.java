package com.aegis.operations.store.elasticsearch;

import com.aegis.operations.model.Facility;
import com.aegis.operations.model.FacilityKind;
import com.aegis.operations.model.FacilityStatus;
import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentDeclarationLink;
import com.aegis.operations.model.IncidentKind;
import com.aegis.operations.model.IncidentSource;
import com.aegis.operations.model.IncidentStatus;
import com.aegis.operations.model.JsonEnum;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.model.RecommendationStatus;
import com.aegis.operations.model.RegionCoordinates;
import com.aegis.operations.model.Resource;
import com.aegis.operations.model.ResourceKind;
import com.aegis.operations.model.Severity;
import com.aegis.operations.store.elasticsearch.document.FacilityDocument;
import com.aegis.operations.store.elasticsearch.document.DisasterDeclarationDocument;
import com.aegis.operations.store.elasticsearch.document.IncidentDocument;
import com.aegis.operations.store.elasticsearch.document.IncidentDeclarationLinkDocument;
import com.aegis.operations.store.elasticsearch.document.RecommendationDocument;
import com.aegis.operations.store.elasticsearch.document.RegionCoordinatesDocument;
import com.aegis.operations.store.elasticsearch.document.ResourceDocument;
import java.util.List;

final class ElasticsearchDocumentMapper {
    private ElasticsearchDocumentMapper() {
    }

    static IncidentDocument toDocument(Incident incident) {
        IncidentDocument document = new IncidentDocument();
        document.setId(incident.getId());
        document.setTitle(incident.getTitle());
        document.setKind(incident.getKind().jsonValue());
        document.setSeverity(incident.getSeverity().jsonValue());
        document.setLocation(incident.getLocation());
        document.setState(incident.getState());
        document.setStatus(incident.getStatus().jsonValue());
        document.setReportedAt(incident.getReportedAt());
        document.setCoordinates(toDocument(incident.getCoordinates()));
        document.setDescription(incident.getDescription());
        document.setAffectedFacilityIds(incident.getAffectedFacilityIds());
        document.setAssignedResourceIds(incident.getAssignedResourceIds());
        document.setSource(incident.getSource().jsonValue());
        document.setSourceId(incident.getSourceId());
        document.setSourceUrl(incident.getSourceUrl());
        document.setSourceUpdatedAt(incident.getSourceUpdatedAt());
        document.setIngestedAt(incident.getIngestedAt());
        return document;
    }

    static Incident toDomain(IncidentDocument document) {
        return new Incident(document.getId(), document.getTitle(), parse(IncidentKind.class, document.getKind()),
                parse(Severity.class, document.getSeverity()), document.getLocation(), document.getState(),
                parse(IncidentStatus.class, document.getStatus()), document.getReportedAt(),
                toDomain(document.getCoordinates()), document.getDescription(),
                document.getAffectedFacilityIds(), document.getAssignedResourceIds(),
                parseOrDefault(IncidentSource.class, document.getSource(), IncidentSource.DEMO),
                document.getSourceId() == null ? document.getId() : document.getSourceId(), document.getSourceUrl(),
                document.getSourceUpdatedAt(), document.getIngestedAt());
    }

    static DisasterDeclarationDocument toDocument(DisasterDeclaration declaration) {
        DisasterDeclarationDocument document = new DisasterDeclarationDocument();
        document.setId(declaration.getId());
        document.setDisasterNumber(declaration.getDisasterNumber());
        document.setDeclarationType(declaration.getDeclarationType());
        document.setState(declaration.getState());
        document.setTitle(declaration.getTitle());
        document.setIncidentType(declaration.getIncidentType());
        document.setDeclarationDate(declaration.getDeclarationDate());
        document.setIncidentBeginDate(declaration.getIncidentBeginDate());
        document.setIncidentEndDate(declaration.getIncidentEndDate());
        document.setDeclaredAreas(declaration.getDeclaredAreas());
        document.setIndividualAssistanceDeclared(declaration.isIndividualAssistanceDeclared());
        document.setPublicAssistanceDeclared(declaration.isPublicAssistanceDeclared());
        document.setHazardMitigationDeclared(declaration.isHazardMitigationDeclared());
        document.setSource(declaration.getSource());
        document.setSourceId(declaration.getSourceId());
        document.setSourceUpdatedAt(declaration.getSourceUpdatedAt());
        document.setIngestedAt(declaration.getIngestedAt());
        return document;
    }

    static DisasterDeclaration toDomain(DisasterDeclarationDocument document) {
        return new DisasterDeclaration(document.getId(), document.getDisasterNumber(), document.getDeclarationType(),
                document.getState(), document.getTitle(), document.getIncidentType(), document.getDeclarationDate(),
                document.getIncidentBeginDate(), document.getIncidentEndDate(), document.getDeclaredAreas(),
                document.isIndividualAssistanceDeclared(), document.isPublicAssistanceDeclared(),
                document.isHazardMitigationDeclared(), document.getSource(), document.getSourceId(),
                document.getSourceUpdatedAt(), document.getIngestedAt());
    }

    static IncidentDeclarationLinkDocument toDocument(IncidentDeclarationLink link) {
        IncidentDeclarationLinkDocument document = new IncidentDeclarationLinkDocument();
        document.setId(link.getId());
        document.setIncidentId(link.getIncidentId());
        document.setDeclarationId(link.getDeclarationId());
        document.setConfidence(link.getConfidence());
        document.setReasons(link.getReasons());
        return document;
    }

    static IncidentDeclarationLink toDomain(IncidentDeclarationLinkDocument document) {
        return new IncidentDeclarationLink(document.getIncidentId(), document.getDeclarationId(),
                document.getConfidence(), document.getReasons());
    }

    static ResourceDocument toDocument(Resource resource) {
        ResourceDocument document = new ResourceDocument();
        document.setId(resource.getId());
        document.setKind(resource.getKind().jsonValue());
        document.setLabel(resource.getLabel());
        document.setAvailable(resource.getAvailable());
        document.setTotal(resource.getTotal());
        document.setUnit(resource.getUnit());
        return document;
    }

    static Resource toDomain(ResourceDocument document) {
        return new Resource(document.getId(), parse(ResourceKind.class, document.getKind()), document.getLabel(),
                document.getAvailable(), document.getTotal(), document.getUnit());
    }

    static FacilityDocument toDocument(Facility facility) {
        FacilityDocument document = new FacilityDocument();
        document.setId(facility.getId());
        document.setName(facility.getName());
        document.setKind(facility.getKind().jsonValue());
        document.setStatus(facility.getStatus().jsonValue());
        document.setCoordinates(toDocument(facility.getCoordinates()));
        return document;
    }

    static Facility toDomain(FacilityDocument document) {
        return new Facility(document.getId(), document.getName(), parse(FacilityKind.class, document.getKind()),
                parse(FacilityStatus.class, document.getStatus()), toDomain(document.getCoordinates()));
    }

    static RecommendationDocument toDocument(Recommendation recommendation) {
        RecommendationDocument document = new RecommendationDocument();
        document.setId(recommendation.getId());
        document.setPriority(recommendation.getPriority().jsonValue());
        document.setTitle(recommendation.getTitle());
        document.setDetail(recommendation.getDetail());
        document.setActionLabel(recommendation.getActionLabel());
        document.setIncidentId(recommendation.getIncidentId());
        document.setResourceId(recommendation.getResourceId());
        document.setStatus(recommendation.getStatus().jsonValue());
        document.setStatusMessage(recommendation.getStatusMessage());
        return document;
    }

    static Recommendation toDomain(RecommendationDocument document) {
        return new Recommendation(document.getId(), parse(Severity.class, document.getPriority()),
                document.getTitle(), document.getDetail(), document.getActionLabel(), document.getIncidentId(),
                document.getResourceId(), parse(RecommendationStatus.class, document.getStatus()),
                document.getStatusMessage());
    }

    private static RegionCoordinatesDocument toDocument(RegionCoordinates coordinates) {
        if (coordinates == null) {
            return null;
        }
        return new RegionCoordinatesDocument(coordinates.x(), coordinates.y());
    }

    private static RegionCoordinates toDomain(RegionCoordinatesDocument document) {
        if (document == null) {
            return null;
        }
        return new RegionCoordinates(document.getX(), document.getY());
    }

    private static <T extends Enum<T> & JsonEnum> T parse(Class<T> enumType, String value) {
        return List.of(enumType.getEnumConstants()).stream()
                .filter(candidate -> candidate.jsonValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported " + enumType.getSimpleName()
                        + " value: " + value));
    }

    private static <T extends Enum<T> & JsonEnum> T parseOrDefault(Class<T> enumType, String value, T fallback) {
        if (value == null) {
            return fallback;
        }
        return parse(enumType, value);
    }
}
