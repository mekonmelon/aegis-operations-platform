export type Severity = 'critical' | 'high' | 'moderate' | 'low'
export type IncidentStatus = 'Escalating' | 'Response active' | 'Monitoring' | 'Contained'
export type IncidentKind = 'flood' | 'wildfire' | 'outage' | 'road' | 'weather'
export type IncidentSource = 'demo' | 'nws'
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
  coordinates?: Coordinates | null
  description: string
  affectedFacilityIds: string[]
  assignedResourceIds: string[]
  source: IncidentSource
  sourceId?: string | null
  sourceUrl?: string | null
  sourceUpdatedAt?: string | null
  ingestedAt?: string | null
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

export interface DisasterDeclaration {
  id: string
  disasterNumber: number
  declarationType: string
  state: string
  title: string
  incidentType: string
  declarationDate: string
  incidentBeginDate?: string | null
  incidentEndDate?: string | null
  declaredAreas: string[]
  individualAssistanceDeclared: boolean
  publicAssistanceDeclared: boolean
  hazardMitigationDeclared: boolean
  source: 'fema'
  sourceId: string
  sourceUpdatedAt?: string | null
  ingestedAt?: string | null
}

export interface IncidentDeclarationMatch {
  incidentId: string
  declaration: DisasterDeclaration
  confidence: number
  reasons: string[]
}

export interface IncidentFilters {
  search: string
  severity: Severity | 'all'
  kind: IncidentKind | 'all'
  status: IncidentStatus | 'all'
}
