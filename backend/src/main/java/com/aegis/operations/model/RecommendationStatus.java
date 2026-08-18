package com.aegis.operations.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RecommendationStatus implements JsonEnum {
    PENDING("pending"),
    APPROVED("approved"),
    DISMISSED("dismissed");

    private final String jsonValue;

    RecommendationStatus(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    @JsonValue
    public String jsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static RecommendationStatus fromJson(String value) {
        return EnumParser.parse(RecommendationStatus.class, value);
    }
}
