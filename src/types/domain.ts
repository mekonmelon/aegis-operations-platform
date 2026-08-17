export type Severity = 'critical' | 'high' | 'moderate' | 'low'
export type IncidentStatus = 'Escalating' | 'Response active' | 'Monitoring' | 'Contained'
export type IncidentKind = 'flood' | 'wildfire' | 'outage' | 'road'
export type RecommendationStatus = 'pending' | 'approved' | 'dismissed'

export interface Coordinates { x: number; y: number }

export interface Incident {
  id: string
  title: string
  kind: IncidentKind
  severity: Severity
  location: string
  status: IncidentStatus
  reportedAt: string
  coordinates: Coordinates
  description: string
  affectedFacilityIds: string[]
  assignedResourceIds: string[]
}

export type ResourceKind = 'teams' | 'vehicles' | 'medical' | 'supplies'
export interface Resource { id: string; kind: ResourceKind; label: string; available: number; total: number; unit: string }

export type FacilityKind = 'hospital' | 'shelter' | 'depot'
export interface Facility { id: string; name: string; kind: FacilityKind; status: 'operational' | 'at-risk'; coordinates: Coordinates }

export interface Recommendation {
  id: string
  priority: Severity
  title: string
  detail: string
  actionLabel: string
  incidentId: string
  resourceId: string
  status: RecommendationStatus
  statusMessage?: string
}

export interface DashboardData { incidents: Incident[]; resources: Resource[]; facilities: Facility[]; recommendations: Recommendation[]; lastUpdated: string }

export interface IncidentFilters {
  search: string
  severity: Severity | 'all'
  kind: IncidentKind | 'all'
  status: IncidentStatus | 'all'
}
