package com.aegis.operations.store.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "aegis-facilities")
public class FacilityDocument {
    @Id
    private String id;
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Keyword)
    private String kind;
    @Field(type = FieldType.Keyword)
    private String status;
    @Field(type = FieldType.Object)
    private RegionCoordinatesDocument coordinates;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public RegionCoordinatesDocument getCoordinates() { return coordinates; }
    public void setCoordinates(RegionCoordinatesDocument coordinates) { this.coordinates = coordinates; }
}
