package com.aegis.operations.integration.fema;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@ConfigurationProperties(prefix = "aegis.ingestion.fema")
public class FemaIngestionProperties {
    private String baseUrl = "https://www.fema.gov/api/open/v1";
    private List<String> states = List.of("NJ", "NY", "PA");
    private Duration recentWindow = Duration.ofDays(730);

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public List<String> getStates() { return states; }
    public void setStates(List<String> states) { this.states = states; }
    public Duration getRecentWindow() { return recentWindow; }
    public void setRecentWindow(Duration recentWindow) { this.recentWindow = recentWindow; }

    public List<String> effectiveStates() {
        String environmentStates = System.getenv("AEGIS_FEMA_STATES");
        if (StringUtils.hasText(environmentStates)) {
            return Arrays.stream(environmentStates.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .map(String::toUpperCase)
                    .distinct()
                    .toList();
        }
        return states.stream().map(String::trim).filter(StringUtils::hasText).map(String::toUpperCase).distinct()
                .toList();
    }

    public LocalDate earliestDeclarationDate() {
        Duration window = recentWindow == null ? Duration.ofDays(730) : recentWindow;
        return LocalDate.now().minusDays(window.toDays());
    }
}
