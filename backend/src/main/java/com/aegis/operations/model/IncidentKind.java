package com.aegis.operations.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IncidentKind implements JsonEnum {
    FLOOD("flood"),
    WILDFIRE("wildfire"),
    OUTAGE("outage"),
    ROAD("road");

    private final String jsonValue;

    IncidentKind(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    @JsonValue
    public String jsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static IncidentKind fromJson(String value) {
        return EnumParser.parse(IncidentKind.class, value);
    }
}
