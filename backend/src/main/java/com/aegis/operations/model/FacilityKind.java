package com.aegis.operations.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FacilityKind implements JsonEnum {
    HOSPITAL("hospital"),
    SHELTER("shelter"),
    DEPOT("depot");

    private final String jsonValue;

    FacilityKind(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    @JsonValue
    public String jsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static FacilityKind fromJson(String value) {
        return EnumParser.parse(FacilityKind.class, value);
    }
}
