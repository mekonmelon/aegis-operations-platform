package com.aegis.operations.model;

public class Resource {
    private String id;
    private ResourceKind kind;
    private String label;
    private int available;
    private int total;
    private String unit;

    public Resource() {
    }

    public Resource(String id, ResourceKind kind, String label, int available, int total, String unit) {
        this.id = id;
        this.kind = kind;
        this.label = label;
        this.available = available;
        this.total = total;
        this.unit = unit;
    }

    public Resource copy() {
        return new Resource(id, kind, label, available, total, unit);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public ResourceKind getKind() { return kind; }
    public void setKind(ResourceKind kind) { this.kind = kind; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
