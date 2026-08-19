# Aegis REST API Contract

This document describes a proposed REST API contract for the current Aegis frontend. It is intentionally scoped to the working demo application and is designed so a future `ApiOperationsDataSource` can replace `MockOperationsDataSource` without major UI changes.

This contract does not introduce authentication, event streaming, external map services, GraphQL, microservices, databases, or backend infrastructure.

## Design Recommendation

Use one aggregate endpoint, `GET /api/dashboard`, as the primary frontend bootstrap call.

The current dashboard renders incidents, resources, facilities, recommendations, and `lastUpdated` together. A single aggregate endpoint keeps the initial UI simple, avoids request waterfalls, and matches the existing `OperationsDataSource.getDashboardData(): Promise<DashboardData>` method.

Separate resource endpoints should still exist for direct reads and future screens. They are useful for refreshing only one panel, linking to a detail route, or supporting a future API client that loads incident details independently. For this iteration, the frontend can continue to use the aggregate endpoint for dashboard load and the recommendation mutation endpoints for state changes.

Tradeoff:
- `GET /api/dashboard` is efficient and maps directly to the current UI, but returns more data than a single panel may need.
- Separate endpoints are more flexible and cacheable per resource, but add orchestration complexity to the frontend.

Recommendation: implement both, but make `GET /api/dashboard` the primary contract for the current operations dashboard.

## Existing Frontend Types Reviewed

Current TypeScript domain types:
- `Severity`: `critical | high | moderate | low`
- `IncidentStatus`: API values are `escalating | response_active | monitoring | contained`; the current frontend maps these to display labels.
- `IncidentKind`: `flood | wildfire | outage | road | weather`
- `IncidentSource`: `demo | nws`
- `RecommendationStatus`: `pending | approved | dismissed`
- `Coordinates`: `{ x: number; y: number }`
- `Incident`
- `Resource`
- `Facility`
- `Recommendation`
- `DashboardData`
- `IncidentFilters`

Current data-source interface:

```ts
export interface OperationsDataSource {
  getDashboardData(): Promise<DashboardData>
  approveRecommendation(recommendationId: string): Promise<DashboardData>
  dismissRecommendation(recommendationId: string): Promise<DashboardData>
}
```

## API Conventions

Base path: `/api`

Response format: JSON

Timestamps: ISO 8601 strings in UTC, for example `2026-08-17T14:32:00Z`

IDs: stable string identifiers such as `INC-2048`, `RES-1`, `FAC-2`, and `REC-1`

Errors should use a simple JSON shape:

```json
{
  "error": {
    "code": "RECOMMENDATION_NOT_FOUND",
    "message": "Recommendation REC-9999 was not found."
  }
}
```

## Source Model

Aegis keeps heterogeneous public sources in separate domain models:

- NWS active alerts become `Incident` records because they describe operational, time-sensitive alerts.
- OpenFEMA disaster declarations become `DisasterDeclaration` records because they are official declarations that may group many counties and cover a broader incident period.

Relationships between those entities are represented by `IncidentDeclarationLink` records, not by merging FEMA data into incidents.

## Endpoints

### Get Dashboard

Method: `GET`

URL: `/api/dashboard`

Purpose: Load the complete operations dashboard in one request.

Request body: none

Response body:

```json
{
  "lastUpdated": "2026-08-17T14:32:00Z",
  "incidents": [],
  "resources": [],
  "facilities": [],
  "recommendations": []
}
```

Likely status codes:
- `200 OK`: dashboard loaded
- `500 Internal Server Error`: dashboard data could not be loaded

### List Incidents

Method: `GET`

URL: `/api/incidents`

Purpose: Return incidents for the active operations period. Optional query parameters can support server-side filtering later, though current frontend filtering can remain local.

Optional query parameters:
- `search`: text search over title and location
- `severity`: `critical | high | moderate | low`
- `kind`: `flood | wildfire | outage | road | weather`
- `status`: `escalating | response_active | monitoring | contained`
- `source`: `demo | nws`

Request body: none

Response body:

```json
{
  "incidents": []
}
```

Likely status codes:
- `200 OK`: incidents loaded
- `400 Bad Request`: invalid filter value
- `500 Internal Server Error`: incidents could not be loaded

### Get Incident Details

Method: `GET`

URL: `/api/incidents/{incidentId}`

Purpose: Return one incident by id. The response includes the same fields used by the current details panel.

Request body: none

Response body:

```json
{
  "id": "INC-2048",
  "title": "River District Flooding",
  "kind": "flood",
  "severity": "critical",
  "location": "North River District",
  "status": "escalating",
  "reportedAt": "2026-08-17T14:18:00Z",
  "coordinates": { "x": 31, "y": 30 },
  "description": "Rapid river rise is flooding low-lying residential blocks and threatening access to the north evacuation route.",
  "affectedFacilityIds": ["FAC-2"],
  "assignedResourceIds": [],
  "source": "demo",
  "sourceId": "INC-2048",
  "sourceUrl": null,
  "sourceUpdatedAt": "2026-08-17T14:18:00Z",
  "ingestedAt": "2026-08-17T14:18:00Z"
}
```

Likely status codes:
- `200 OK`: incident found
- `404 Not Found`: incident id does not exist
- `500 Internal Server Error`: incident could not be loaded

### List Resources

Method: `GET`

URL: `/api/resources`

Purpose: Return current resource availability for response teams, vehicles, medical capacity, and supplies.

Request body: none

Response body:

```json
{
  "resources": []
}
```

Likely status codes:
- `200 OK`: resources loaded
- `500 Internal Server Error`: resources could not be loaded

### List Facilities

Method: `GET`

URL: `/api/facilities`

Purpose: Return monitored facilities for map markers and incident detail lookups.

Request body: none

Response body:

```json
{
  "facilities": []
}
```

Likely status codes:
- `200 OK`: facilities loaded
- `500 Internal Server Error`: facilities could not be loaded

### List Recommendations

Method: `GET`

URL: `/api/recommendations`

Purpose: Return current operational recommendations.

Optional query parameters:
- `status`: `pending | approved | dismissed`
- `incidentId`: incident id

Request body: none

Response body:

```json
{
  "recommendations": []
}
```

Likely status codes:
- `200 OK`: recommendations loaded
- `400 Bad Request`: invalid query parameter
- `500 Internal Server Error`: recommendations could not be loaded

### Approve Recommendation

Method: `POST`

URL: `/api/recommendations/{recommendationId}/approve`

Purpose: Approve a pending recommendation. For the current app, approving a deployment recommendation may update the recommendation status, assign the referenced resource to the incident, reduce resource availability, update incident status, and return the refreshed dashboard.

Request body:

```json
{
  "approvedBy": "demo-operator",
  "note": "Approved from operations dashboard."
}
```

The request body can be optional for the first API version. It is included so the Java API has a natural place for operator metadata later.

Response body:

```json
{
  "lastUpdated": "2026-08-17T14:36:11Z",
  "incidents": [],
  "resources": [],
  "facilities": [],
  "recommendations": []
}
```

Likely status codes:
- `200 OK`: recommendation approved and refreshed dashboard returned
- `400 Bad Request`: recommendation cannot be approved from its current state
- `404 Not Found`: recommendation id does not exist
- `409 Conflict`: recommendation was already approved or dismissed by another operation
- `500 Internal Server Error`: approval failed

### Dismiss Recommendation

Method: `POST`

URL: `/api/recommendations/{recommendationId}/dismiss`

Purpose: Dismiss a pending recommendation and return the refreshed dashboard.

Request body:

```json
{
  "dismissedBy": "demo-operator",
  "reason": "Resource commander deferred deployment."
}
```

The request body can be optional for the first API version.

Response body:

```json
{
  "lastUpdated": "2026-08-17T14:37:02Z",
  "incidents": [],
  "resources": [],
  "facilities": [],
  "recommendations": []
}
```

Likely status codes:
- `200 OK`: recommendation dismissed and refreshed dashboard returned
- `400 Bad Request`: recommendation cannot be dismissed from its current state
- `404 Not Found`: recommendation id does not exist
- `409 Conflict`: recommendation was already approved or dismissed by another operation
- `500 Internal Server Error`: dismissal failed

### Refresh NWS Alerts

Method: `POST`

URL: `/api/ingestion/nws/refresh`

Purpose: Trigger a development/admin synchronization of active National Weather Service alerts for the configured areas. A successful sync upserts active NWS alerts as `Incident` records, removes stale NWS-sourced incidents that are no longer active, and does not modify `demo` incidents.

Request body: none

Response body:

```json
{
  "source": "NWS",
  "fetched": 25,
  "created": 8,
  "updated": 17,
  "removed": 3,
  "startedAt": "2026-08-17T15:02:11Z",
  "completedAt": "2026-08-17T15:02:14Z",
  "status": "success",
  "error": null
}
```

Likely status codes:
- `200 OK`: refresh completed
- `500 Internal Server Error`: NWS fetch or normalization failed; existing NWS incidents are preserved

### Get NWS Ingestion Status

Method: `GET`

URL: `/api/ingestion/nws/status`

Purpose: Return the latest simple ingestion status for the NWS data source.

Request body: none

Response body:

```json
{
  "source": "NWS",
  "enabled": false,
  "scheduledEnabled": false,
  "areas": ["NJ", "NY", "PA"],
  "lastAttempt": "2026-08-17T15:02:11Z",
  "lastSuccessfulSync": "2026-08-17T15:02:14Z",
  "lastResult": {
    "source": "NWS",
    "fetched": 25,
    "created": 8,
    "updated": 17,
    "removed": 3,
    "startedAt": "2026-08-17T15:02:11Z",
    "completedAt": "2026-08-17T15:02:14Z",
    "status": "success",
    "error": null
  },
  "lastError": null
}
```

Likely status codes:
- `200 OK`: status loaded
- `500 Internal Server Error`: status could not be loaded

### Refresh FEMA Declarations

Method: `POST`

URL: `/api/ingestion/fema/refresh`

Purpose: Fetch recent OpenFEMA Disaster Declarations Summaries for configured states, group county records by disaster number, upsert `DisasterDeclaration` records, and rebuild explainable declaration links.

Request body: none

Response body:

```json
{
  "source": "FEMA",
  "fetchedRecords": 46,
  "declarations": 8,
  "created": 2,
  "updated": 6,
  "linksCreated": 4,
  "startedAt": "2026-08-18T14:00:00Z",
  "completedAt": "2026-08-18T14:00:02Z",
  "status": "success",
  "error": null
}
```

Likely status codes:
- `200 OK`: refresh completed
- `500 Internal Server Error`: FEMA fetch or normalization failed; existing FEMA data and links are preserved

### Get FEMA Ingestion Status

Method: `GET`

URL: `/api/ingestion/fema/status`

Purpose: Return latest FEMA ingestion status.

Likely status codes:
- `200 OK`: status loaded
- `500 Internal Server Error`: status could not be loaded

### List Disaster Declarations

Method: `GET`

URL: `/api/declarations`

Optional query parameters:
- `search`: text search over title and declared areas
- `state`: state code, such as `NJ`
- `incidentType`: FEMA incident type, such as `Flood`
- `declarationType`: FEMA declaration type, such as `DR`

Response body:

```json
{
  "declarations": []
}
```

### Get Disaster Declaration

Method: `GET`

URL: `/api/declarations/{declarationId}`

Response body: `DisasterDeclaration`

Likely status codes:
- `200 OK`: declaration found
- `404 Not Found`: declaration does not exist

### Get Incident Declaration Links

Method: `GET`

URL: `/api/incidents/{incidentId}/declarations`

Purpose: Return plausible FEMA declaration relationships for an incident.

Response body:

```json
{
  "declarations": [
    {
      "incidentId": "NWS-673F0EA0A9E18E51",
      "declaration": {},
      "confidence": 0.95,
      "reasons": ["same_state", "compatible_hazard", "overlapping_time_window"]
    }
  ]
}
```

### Get Declaration Incident Links

Method: `GET`

URL: `/api/declarations/{declarationId}/incidents`

Purpose: Return plausible incident relationships for a FEMA declaration.

## Example JSON Models

### Incident

```json
{
  "id": "INC-2048",
  "title": "River District Flooding",
  "kind": "flood",
  "severity": "critical",
  "location": "North River District",
  "status": "escalating",
  "reportedAt": "2026-08-17T14:18:00Z",
  "coordinates": {
    "x": 31,
    "y": 30
  },
  "description": "Rapid river rise is flooding low-lying residential blocks and threatening access to the north evacuation route.",
  "affectedFacilityIds": ["FAC-2"],
  "assignedResourceIds": ["RES-1"],
  "source": "demo",
  "sourceId": "INC-2048",
  "sourceUrl": null,
  "sourceUpdatedAt": "2026-08-17T14:18:00Z",
  "ingestedAt": "2026-08-17T14:18:00Z"
}
```

NWS-imported incidents use the same model. They may have `coordinates: null` because the current frontend map uses schematic regional x/y points, not real geospatial geometry.

```json
{
  "id": "NWS-673F0EA0A9E18E51",
  "title": "Severe Thunderstorm Warning for Camden County",
  "kind": "weather",
  "severity": "high",
  "location": "Camden County",
  "status": "monitoring",
  "reportedAt": "2026-08-17T15:00:00Z",
  "coordinates": null,
  "description": "National Weather Service alert text and operator instructions.",
  "affectedFacilityIds": [],
  "assignedResourceIds": [],
  "source": "nws",
  "sourceId": "https://api.weather.gov/alerts/urn:oid:example",
  "sourceUrl": "https://api.weather.gov/alerts/urn:oid:example",
  "sourceUpdatedAt": "2026-08-17T14:50:00Z",
  "ingestedAt": "2026-08-17T15:02:14Z"
}
```

### Resource

```json
{
  "id": "RES-1",
  "kind": "teams",
  "label": "Response Teams",
  "available": 13,
  "total": 20,
  "unit": "teams"
}
```

### DisasterDeclaration

```json
{
  "id": "FEMA-4926",
  "disasterNumber": 4926,
  "declarationType": "DR",
  "state": "NJ",
  "title": "Severe Storms and Flooding",
  "incidentType": "Flood",
  "declarationDate": "2026-08-03T00:00:00Z",
  "incidentBeginDate": "2026-07-28",
  "incidentEndDate": "2026-08-02",
  "declaredAreas": ["Burlington County", "Camden County"],
  "individualAssistanceDeclared": true,
  "publicAssistanceDeclared": true,
  "hazardMitigationDeclared": false,
  "source": "fema",
  "sourceId": "4926",
  "sourceUpdatedAt": "2026-08-03T00:00:00Z",
  "ingestedAt": "2026-08-18T14:00:02Z"
}
```

### IncidentDeclarationLink

```json
{
  "incidentId": "NWS-673F0EA0A9E18E51",
  "declarationId": "FEMA-4926",
  "confidence": 0.95,
  "reasons": ["same_state", "compatible_hazard", "overlapping_time_window"]
}
```

### Facility

```json
{
  "id": "FAC-2",
  "name": "Northside Clinic",
  "kind": "hospital",
  "status": "at-risk",
  "coordinates": {
    "x": 42,
    "y": 36
  }
}
```

### Recommendation

```json
{
  "id": "REC-1",
  "priority": "critical",
  "title": "Deploy Team Alpha-3",
  "detail": "Deploy swift-water rescue team Alpha-3 to North River District. Rising water levels may isolate 340 residents within 45 minutes.",
  "actionLabel": "Review deployment",
  "incidentId": "INC-2048",
  "resourceId": "RES-1",
  "status": "approved",
  "statusMessage": "Deployment approved. Resource assignment is reflected in the incident record."
}
```

### Dashboard Response

```json
{
  "lastUpdated": "2026-08-17T14:36:11Z",
  "incidents": [
    {
      "id": "INC-2048",
      "title": "River District Flooding",
      "kind": "flood",
      "severity": "critical",
      "location": "North River District",
      "status": "response_active",
      "reportedAt": "2026-08-17T14:18:00Z",
      "coordinates": { "x": 31, "y": 30 },
      "description": "Rapid river rise is flooding low-lying residential blocks and threatening access to the north evacuation route.",
      "affectedFacilityIds": ["FAC-2"],
      "assignedResourceIds": ["RES-1"],
      "source": "demo",
      "sourceId": "INC-2048",
      "sourceUrl": null,
      "sourceUpdatedAt": "2026-08-17T14:18:00Z",
      "ingestedAt": "2026-08-17T14:18:00Z"
    }
  ],
  "resources": [
    {
      "id": "RES-1",
      "kind": "teams",
      "label": "Response Teams",
      "available": 13,
      "total": 20,
      "unit": "teams"
    }
  ],
  "facilities": [
    {
      "id": "FAC-2",
      "name": "Northside Clinic",
      "kind": "hospital",
      "status": "at-risk",
      "coordinates": { "x": 42, "y": 36 }
    }
  ],
  "recommendations": [
    {
      "id": "REC-1",
      "priority": "critical",
      "title": "Deploy Team Alpha-3",
      "detail": "Deploy swift-water rescue team Alpha-3 to North River District. Rising water levels may isolate 340 residents within 45 minutes.",
      "actionLabel": "Review deployment",
      "incidentId": "INC-2048",
      "resourceId": "RES-1",
      "status": "approved",
      "statusMessage": "Deployment approved. Resource assignment is reflected in the incident record."
    }
  ]
}
```

## TypeScript Mapping Notes

Types that can map directly to API responses:
- `Coordinates`
- `Incident`
- `Resource`
- `Facility`
- `Recommendation`
- `DashboardData`
- String union types for `Severity`, `IncidentKind`, `IncidentStatus`, `RecommendationStatus`, `ResourceKind`, and `FacilityKind`
- `IncidentSource`

Types that should remain frontend-only:
- `IncidentFilters`

Recommended future type changes:
- Split API response DTOs from UI/domain view models once the Java API is implemented. For now, direct mapping is acceptable because the UI is small.
- Keep API `IncidentStatus` values as stable enum-style strings such as `escalating`, `response_active`, `monitoring`, and `contained`; map them to UI display labels in the frontend API data-source layer.
- Keep `source`, `sourceId`, `sourceUrl`, `sourceUpdatedAt`, and `ingestedAt` on the generic `Incident` model. Do not add source-specific fields such as `nwsHeadline` to the core model.
- Consider changing facility status from inline string literals to a named `FacilityStatus` type.
- Consider replacing map placeholder `Coordinates` with a named `MapPoint` or `RegionCoordinates` type while this remains an offline schematic map. Do not use latitude/longitude names until the product actually integrates a real map service.
- Allow `Incident.coordinates` to be `null` because imported external incidents may not have schematic map placement.
- Keep `affectedFacilityIds` and `assignedResourceIds` as id arrays for now. They make the aggregate dashboard compact and avoid duplicating facility/resource objects inside each incident.

## NWS Import Contract

NWS ingestion is intentionally not part of the frontend `OperationsDataSource` yet. It is a backend development/admin capability.

Configuration:
- `aegis.ingestion.nws.areas=NJ,NY,PA`
- `AEGIS_NWS_AREAS=NJ,NY,PA` overrides the configured area list
- `AEGIS_NWS_USER_AGENT` overrides the configured User-Agent
- `aegis.ingestion.nws.enabled=true` performs a one-shot refresh after backend startup
- `aegis.ingestion.nws.scheduled-enabled=true` enables scheduled refreshes
- `aegis.ingestion.nws.refresh-interval=5m` controls the schedule and is clamped to a minimum of 30 seconds

Normalization rules:
- NWS `Extreme` severity maps to Aegis `critical`
- NWS `Severe` maps to `high`
- NWS `Moderate` maps to `moderate`
- NWS `Minor`, unknown, or missing severity maps to `low`
- `Flash Flood Warning`, `Flood Warning`, and `Flood Advisory` map to `flood`
- Other NWS alerts map to `weather`
- Imported NWS incidents default to `monitoring`
- Internal IDs are deterministic `NWS-...` IDs derived from the external NWS alert ID; the full original ID is preserved in `sourceId`

## FEMA Import And Correlation Contract

Configuration:
- `aegis.ingestion.fema.base-url=https://www.fema.gov/api/open/v1`
- `aegis.ingestion.fema.states=NJ,NY,PA`
- `AEGIS_FEMA_STATES=NJ,NY,PA` overrides the configured state list
- `aegis.ingestion.fema.recent-window=730d` limits the query window

Grouping and deduplication:
- FEMA records are grouped by `disasterNumber`
- one Aegis ID is created per disaster number, for example `FEMA-4926`
- `designatedArea` values become `declaredAreas`
- repeated ingestion updates the same declaration instead of duplicating it

Correlation scoring:
- same state: `+0.40`
- compatible hazard: `+0.35`
- incident timestamp within or near FEMA incident period: `+0.20`
- declared-area text appears in incident location: `+0.05`
- minimum exposed confidence: `0.70`

Limitations:
- Links are plausible operational relationships, not definitive legal or scientific matches.
- Rules are deterministic and explainable. No LLM or ML model is used.
- This design can later move to Spark by running the same source-specific normalization and link-scoring logic over larger historical or streaming datasets.

## Future ApiOperationsDataSource Shape

The future frontend data source can keep the same methods:

```ts
getDashboardData(): Promise<DashboardData>
approveRecommendation(recommendationId: string): Promise<DashboardData>
dismissRecommendation(recommendationId: string): Promise<DashboardData>
```

Suggested mapping:
- `getDashboardData()` calls `GET /api/dashboard`
- `approveRecommendation(id)` calls `POST /api/recommendations/{id}/approve`
- `dismissRecommendation(id)` calls `POST /api/recommendations/{id}/dismiss`

Because the mutation endpoints return the refreshed dashboard, the current React hook can keep replacing its local `DashboardData` snapshot after each operation.
