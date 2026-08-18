package com.aegis.operations.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FacilityStatus implements JsonEnum {
    OPERATIONAL("operational"),
    AT_RISK("at-risk");

    private final String jsonValue;

    FacilityStatus(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    @JsonValue
    public String jsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static FacilityStatus fromJson(String value) {
        return EnumParser.parse(FacilityStatus.class, value);
    }
}
