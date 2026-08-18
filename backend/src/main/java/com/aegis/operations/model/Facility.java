package com.aegis.operations.model;

public class Facility {
    private String id;
    private String name;
    private FacilityKind kind;
    private FacilityStatus status;
    private RegionCoordinates coordinates;

    public Facility() {
    }

    public Facility(String id, String name, FacilityKind kind, FacilityStatus status, RegionCoordinates coordinates) {
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.status = status;
        this.coordinates = coordinates;
    }

    public Facility copy() {
        return new Facility(id, name, kind, status, coordinates);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public FacilityKind getKind() { return kind; }
    public void setKind(FacilityKind kind) { this.kind = kind; }
    public FacilityStatus getStatus() { return status; }
    public void setStatus(FacilityStatus status) { this.status = status; }
    public RegionCoordinates getCoordinates() { return coordinates; }
    public void setCoordinates(RegionCoordinates coordinates) { this.coordinates = coordinates; }
}
