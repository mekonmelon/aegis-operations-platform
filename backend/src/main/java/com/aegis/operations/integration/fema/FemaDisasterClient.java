package com.aegis.operations.integration.fema;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FemaDisasterClient {
    private final RestClient femaRestClient;

    public FemaDisasterClient(@Qualifier("femaRestClient") RestClient femaRestClient) {
        this.femaRestClient = femaRestClient;
    }

    public List<FemaDisasterDto> fetchRecentDeclarations(List<String> states, java.time.LocalDate earliestDate) {
        List<FemaDisasterDto> records = new ArrayList<>();
        for (String state : states) {
            String filter = "state eq '" + state + "' and declarationDate ge '"
                    + earliestDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "T00:00:00.000Z'";
            FemaDisasterResponse response = femaRestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/DisasterDeclarationsSummaries")
                            .queryParam("$filter", filter)
                            .queryParam("$orderby", "declarationDate desc")
                            .queryParam("$top", "500")
                            .build())
                    .retrieve()
                    .body(FemaDisasterResponse.class);
            if (response != null) {
                records.addAll(response.records());
            }
        }
        return records;
    }
}
