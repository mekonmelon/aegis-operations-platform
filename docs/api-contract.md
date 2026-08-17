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
- `IncidentStatus`: `Escalating | Response active | Monitoring | Contained`
- `IncidentKind`: `flood | wildfire | outage | road`
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
- `kind`: `flood | wildfire | outage | road`
- `status`: `Escalating | Response active | Monitoring | Contained`

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
  "status": "Escalating",
  "reportedAt": "2026-08-17T14:18:00Z",
  "coordinates": { "x": 31, "y": 30 },
  "description": "Rapid river rise is flooding low-lying residential blocks and threatening access to the north evacuation route.",
  "affectedFacilityIds": ["FAC-2"],
  "assignedResourceIds": []
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

## Example JSON Models

### Incident

```json
{
  "id": "INC-2048",
  "title": "River District Flooding",
  "kind": "flood",
  "severity": "critical",
  "location": "North River District",
  "status": "Escalating",
  "reportedAt": "2026-08-17T14:18:00Z",
  "coordinates": {
    "x": 31,
    "y": 30
  },
  "description": "Rapid river rise is flooding low-lying residential blocks and threatening access to the north evacuation route.",
  "affectedFacilityIds": ["FAC-2"],
  "assignedResourceIds": ["RES-1"]
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
      "status": "Response active",
      "reportedAt": "2026-08-17T14:18:00Z",
      "coordinates": { "x": 31, "y": 30 },
      "description": "Rapid river rise is flooding low-lying residential blocks and threatening access to the north evacuation route.",
      "affectedFacilityIds": ["FAC-2"],
      "assignedResourceIds": ["RES-1"]
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

Types that should remain frontend-only:
- `IncidentFilters`

Recommended future type changes:
- Split API response DTOs from UI/domain view models once the Java API is implemented. For now, direct mapping is acceptable because the UI is small.
- Consider changing `IncidentStatus` API values to stable enum-style strings such as `escalating`, `response_active`, `monitoring`, and `contained`. The current values are display labels, which are convenient for the UI but less ideal as backend enum values.
- Consider changing facility status from inline string literals to a named `FacilityStatus` type.
- Consider replacing map placeholder `Coordinates` with a named `MapPoint` or `RegionCoordinates` type while this remains an offline schematic map. Do not use latitude/longitude names until the product actually integrates a real map service.
- Keep `affectedFacilityIds` and `assignedResourceIds` as id arrays for now. They make the aggregate dashboard compact and avoid duplicating facility/resource objects inside each incident.

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
