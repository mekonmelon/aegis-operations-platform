package com.aegis.operations.store.elasticsearch.document;

public class RegionCoordinatesDocument {
    private int x;
    private int y;

    public RegionCoordinatesDocument() {
    }

    public RegionCoordinatesDocument(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}
