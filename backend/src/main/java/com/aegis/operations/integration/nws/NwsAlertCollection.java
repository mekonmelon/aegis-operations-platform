package com.aegis.operations.integration.nws;

import java.util.List;

public record NwsAlertCollection(List<NwsAlertFeature> features) {
    public List<NwsAlertFeature> features() {
        return features == null ? List.of() : features;
    }
}
