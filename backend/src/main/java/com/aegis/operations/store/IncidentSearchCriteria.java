package com.aegis.operations.store;

import com.aegis.operations.model.IncidentKind;
import com.aegis.operations.model.IncidentStatus;
import com.aegis.operations.model.Severity;

public record IncidentSearchCriteria(
        String search,
        Severity severity,
        IncidentKind kind,
        IncidentStatus status) {
}
