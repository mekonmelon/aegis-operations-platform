package com.aegis.operations.store.elasticsearch.document;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "aegis-state")
public class DashboardStateDocument {
    public static final String DASHBOARD_ID = "dashboard";

    @Id
    private String id = DASHBOARD_ID;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Instant lastUpdated;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
