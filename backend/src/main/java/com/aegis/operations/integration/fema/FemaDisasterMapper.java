package com.aegis.operations.integration.fema;

import com.aegis.operations.model.DisasterDeclaration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FemaDisasterMapper {
    public List<DisasterDeclaration> toDeclarations(List<FemaDisasterDto> records, Instant ingestedAt) {
        Map<Integer, List<FemaDisasterDto>> byDisasterNumber = new LinkedHashMap<>();
        for (FemaDisasterDto record : records) {
            if (record.disasterNumber() != null) {
                byDisasterNumber.computeIfAbsent(record.disasterNumber(), ignored -> new ArrayList<>()).add(record);
            }
        }

        return byDisasterNumber.entrySet().stream()
                .map(entry -> toDeclaration(entry.getKey(), entry.getValue(), ingestedAt))
                .toList();
    }

    private DisasterDeclaration toDeclaration(Integer disasterNumber, List<FemaDisasterDto> records, Instant ingestedAt) {
        FemaDisasterDto primary = records.stream()
                .max(Comparator.comparing(FemaDisasterDto::declarationDate,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .orElseThrow();
        List<String> areas = records.stream()
                .map(FemaDisasterDto::designatedArea)
                .filter(StringUtils::hasText)
                .map(String::trim)
                .distinct()
                .sorted()
                .toList();
        Instant sourceUpdatedAt = records.stream()
                .map(FemaDisasterDto::lastRefresh)
                .filter(value -> value != null)
                .max(Comparator.naturalOrder())
                .orElse(primary.declarationDate());

        return new DisasterDeclaration(
                "FEMA-" + disasterNumber,
                disasterNumber,
                primary.declarationType(),
                primary.state(),
                firstText(primary.declarationTitle(), primary.incidentType(), "FEMA Disaster Declaration"),
                primary.incidentType(),
                primary.declarationDate(),
                toLocalDate(minInstant(records.stream().map(FemaDisasterDto::incidentBeginDate).toList())),
                toLocalDate(maxInstant(records.stream().map(FemaDisasterDto::incidentEndDate).toList())),
                areas,
                records.stream().anyMatch(record -> Boolean.TRUE.equals(record.iaProgramDeclared())),
                records.stream().anyMatch(record -> Boolean.TRUE.equals(record.paProgramDeclared())),
                records.stream().anyMatch(record -> Boolean.TRUE.equals(record.hmProgramDeclared())),
                "fema",
                String.valueOf(disasterNumber),
                sourceUpdatedAt,
                ingestedAt);
    }

    private static Instant minInstant(List<Instant> values) {
        return values.stream().filter(value -> value != null).min(Comparator.naturalOrder()).orElse(null);
    }

    private static Instant maxInstant(List<Instant> values) {
        return values.stream().filter(value -> value != null).max(Comparator.naturalOrder()).orElse(null);
    }

    private static LocalDate toLocalDate(Instant instant) {
        return instant == null ? null : instant.atZone(ZoneOffset.UTC).toLocalDate();
    }

    private static String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }
}
