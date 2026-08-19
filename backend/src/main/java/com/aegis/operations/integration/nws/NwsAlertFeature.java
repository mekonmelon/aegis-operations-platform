package com.aegis.operations.integration.nws;

import com.fasterxml.jackson.databind.JsonNode;

public record NwsAlertFeature(String id, NwsAlertProperties properties, JsonNode geometry) {
}
