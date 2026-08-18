package com.aegis.operations.store.elasticsearch.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "aegis-resources")
public class ResourceDocument {
    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String kind;
    @Field(type = FieldType.Text)
    private String label;
    @Field(type = FieldType.Integer)
    private int available;
    @Field(type = FieldType.Integer)
    private int total;
    @Field(type = FieldType.Keyword)
    private String unit;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
