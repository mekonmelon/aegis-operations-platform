package com.aegis.operations.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ResourceKind implements JsonEnum {
    TEAMS("teams"),
    VEHICLES("vehicles"),
    MEDICAL("medical"),
    SUPPLIES("supplies");

    private final String jsonValue;

    ResourceKind(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    @JsonValue
    public String jsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static ResourceKind fromJson(String value) {
        return EnumParser.parse(ResourceKind.class, value);
    }
}
