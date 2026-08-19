package com.aegis.operations.integration.nws;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NwsAlertClient {
    private final RestClient nwsRestClient;

    public NwsAlertClient(@Qualifier("nwsRestClient") RestClient nwsRestClient) {
        this.nwsRestClient = nwsRestClient;
    }

    public List<NwsAlertEnvelope> fetchActiveAlerts(List<String> areas) {
        List<NwsAlertEnvelope> alerts = new ArrayList<>();
        for (String area : areas) {
            NwsAlertCollection response = nwsRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/alerts/active").queryParam("area", area).build())
                    .retrieve()
                    .body(NwsAlertCollection.class);
            if (response != null) {
                response.features().forEach(alert -> alerts.add(new NwsAlertEnvelope(area, alert)));
            }
        }
        return alerts;
    }
}
