package com.aegis.operations.service;

import com.aegis.operations.model.DisasterDeclaration;
import com.aegis.operations.model.Incident;
import com.aegis.operations.model.IncidentDeclarationLink;
import com.aegis.operations.model.IncidentKind;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class IncidentDeclarationCorrelationService {
    private static final double MINIMUM_CONFIDENCE = 0.70;

    public IncidentDeclarationLink correlate(Incident incident, DisasterDeclaration declaration) {
        double confidence = 0.0;
        List<String> reasons = new ArrayList<>();

        if (sameState(incident, declaration)) {
            confidence += 0.40;
            reasons.add("same_state");
        }
        if (compatibleHazard(incident, declaration)) {
            confidence += 0.35;
            reasons.add("compatible_hazard");
        }
        if (temporalMatch(incident, declaration)) {
            confidence += 0.20;
            reasons.add("overlapping_time_window");
        }
        if (areaMatch(incident, declaration)) {
            confidence += 0.05;
            reasons.add("overlapping_area_text");
        }

        if (confidence < MINIMUM_CONFIDENCE) {
            return null;
        }
        return new IncidentDeclarationLink(incident.getId(), declaration.getId(), Math.min(confidence, 1.0), reasons);
    }

    private static boolean sameState(Incident incident, DisasterDeclaration declaration) {
        return StringUtils.hasText(incident.getState()) && StringUtils.hasText(declaration.getState())
                && incident.getState().equalsIgnoreCase(declaration.getState());
    }

    private static boolean compatibleHazard(Incident incident, DisasterDeclaration declaration) {
        String incidentType = declaration.getIncidentType() == null ? "" : declaration.getIncidentType().toLowerCase();
        if (incident.getKind() == IncidentKind.FLOOD) {
            return incidentType.contains("flood") || incidentType.contains("storm");
        }
        if (incident.getKind() == IncidentKind.WILDFIRE) {
            return incidentType.contains("fire");
        }
        if (incident.getKind() == IncidentKind.WEATHER) {
            return incidentType.contains("storm") || incidentType.contains("flood") || incidentType.contains("winter")
                    || incidentType.contains("hurricane") || incidentType.contains("tornado");
        }
        return false;
    }

    private static boolean temporalMatch(Incident incident, DisasterDeclaration declaration) {
        if (incident.getReportedAt() == null || declaration.getIncidentBeginDate() == null) {
            return false;
        }
        LocalDate reported = incident.getReportedAt().atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate begin = declaration.getIncidentBeginDate().minusDays(14);
        LocalDate end = declaration.getIncidentEndDate() == null ? declaration.getIncidentBeginDate().plusDays(30)
                : declaration.getIncidentEndDate().plusDays(14);
        return !reported.isBefore(begin) && !reported.isAfter(end);
    }

    private static boolean areaMatch(Incident incident, DisasterDeclaration declaration) {
        if (!StringUtils.hasText(incident.getLocation())) {
            return false;
        }
        String location = incident.getLocation().toLowerCase();
        return declaration.getDeclaredAreas().stream()
                .filter(StringUtils::hasText)
                .map(area -> area.toLowerCase().replace(" county", ""))
                .anyMatch(location::contains);
    }
}
