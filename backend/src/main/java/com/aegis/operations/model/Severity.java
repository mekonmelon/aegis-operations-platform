package com.aegis.operations.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Severity implements JsonEnum {
    CRITICAL("critical"),
    HIGH("high"),
    MODERATE("moderate"),
    LOW("low");

    private final String jsonValue;

    Severity(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    @JsonValue
    public String jsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static Severity fromJson(String value) {
        return EnumParser.parse(Severity.class, value);
    }
}
