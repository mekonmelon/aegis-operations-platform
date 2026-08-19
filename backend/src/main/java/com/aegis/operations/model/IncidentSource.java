package com.aegis.operations.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IncidentSource implements JsonEnum {
    DEMO("demo"),
    NWS("nws");

    private final String jsonValue;

    IncidentSource(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    @JsonValue
    public String jsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static IncidentSource fromJson(String value) {
        return EnumParser.parse(IncidentSource.class, value);
    }
}
