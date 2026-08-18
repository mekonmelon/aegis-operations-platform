package com.aegis.operations.store.elasticsearch;

import com.aegis.operations.model.Facility;
import com.aegis.operations.model.FacilityKind;
import com.aegis.operations.model.FacilityStatus;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentKind;
import com.aegis.operations.model.IncidentStatus;
import com.aegis.operations.model.JsonEnum;
import com.aegis.operations.model.Recommendation;
import com.aegis.operations.model.RecommendationStatus;
import com.aegis.operations.model.RegionCoordinates;
import com.aegis.operations.model.Resource;
import com.aegis.operations.model.ResourceKind;
import com.aegis.operations.model.Severity;
import com.aegis.operations.store.elasticsearch.document.FacilityDocument;
import com.aegis.operations.store.elasticsearch.document.IncidentDocument;
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
        document.setStatus(incident.getStatus().jsonValue());
        document.setReportedAt(incident.getReportedAt());
        document.setCoordinates(toDocument(incident.getCoordinates()));
        document.setDescription(incident.getDescription());
        document.setAffectedFacilityIds(incident.getAffectedFacilityIds());
        document.setAssignedResourceIds(incident.getAssignedResourceIds());
        return document;
    }

    static Incident toDomain(IncidentDocument document) {
        return new Incident(document.getId(), document.getTitle(), parse(IncidentKind.class, document.getKind()),
                parse(Severity.class, document.getSeverity()), document.getLocation(),
                parse(IncidentStatus.class, document.getStatus()), document.getReportedAt(),
                toDomain(document.getCoordinates()), document.getDescription(),
                document.getAffectedFacilityIds(), document.getAssignedResourceIds());
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
        return new RegionCoordinatesDocument(coordinates.x(), coordinates.y());
    }

    private static RegionCoordinates toDomain(RegionCoordinatesDocument document) {
        return new RegionCoordinates(document.getX(), document.getY());
    }

    private static <T extends Enum<T> & JsonEnum> T parse(Class<T> enumType, String value) {
        return List.of(enumType.getEnumConstants()).stream()
                .filter(candidate -> candidate.jsonValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported " + enumType.getSimpleName()
                        + " value: " + value));
    }
}
