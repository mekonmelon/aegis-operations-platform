package com.aegis.operations.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IncidentStatus implements JsonEnum {
    ESCALATING("escalating"),
    RESPONSE_ACTIVE("response_active"),
    MONITORING("monitoring"),
    CONTAINED("contained");

    private final String jsonValue;

    IncidentStatus(String jsonValue) {
        this.jsonValue = jsonValue;
    }

    @Override
    @JsonValue
    public String jsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static IncidentStatus fromJson(String value) {
        return EnumParser.parse(IncidentStatus.class, value);
    }
}
