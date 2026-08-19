package com.aegis.operations.integration.nws;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component("nwsIngestionProperties")
@ConfigurationProperties(prefix = "aegis.ingestion.nws")
public class NwsIngestionProperties {
    private static final long MINIMUM_REFRESH_INTERVAL_MILLIS = 30_000L;

    private boolean enabled = false;
    private boolean scheduledEnabled = false;
    private String baseUrl = "https://api.weather.gov";
    private List<String> areas = List.of("NJ", "NY", "PA");
    private String userAgent = "Aegis Crisis Operations Prototype";
    private Duration refreshInterval = Duration.ofMinutes(5);

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isScheduledEnabled() { return scheduledEnabled; }
    public void setScheduledEnabled(boolean scheduledEnabled) { this.scheduledEnabled = scheduledEnabled; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public List<String> getAreas() { return areas; }
    public void setAreas(List<String> areas) { this.areas = areas; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public Duration getRefreshInterval() { return refreshInterval; }
    public void setRefreshInterval(Duration refreshInterval) { this.refreshInterval = refreshInterval; }

    public List<String> effectiveAreas() {
        String environmentAreas = System.getenv("AEGIS_NWS_AREAS");
        if (StringUtils.hasText(environmentAreas)) {
            return Arrays.stream(environmentAreas.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .map(String::toUpperCase)
                    .distinct()
                    .toList();
        }
        return areas.stream().map(String::trim).filter(StringUtils::hasText).map(String::toUpperCase).distinct()
                .toList();
    }

    public String effectiveUserAgent() {
        String environmentUserAgent = System.getenv("AEGIS_NWS_USER_AGENT");
        return StringUtils.hasText(environmentUserAgent) ? environmentUserAgent : userAgent;
    }

    public long effectiveRefreshIntervalMillis() {
        long configuredInterval = refreshInterval == null ? Duration.ofMinutes(5).toMillis() : refreshInterval.toMillis();
        return Math.max(configuredInterval, MINIMUM_REFRESH_INTERVAL_MILLIS);
    }
}
