import type {
  DashboardData,
  Facility,
  Incident,
  IncidentStatus,
  Recommendation,
  Resource,
} from '../types/domain'
import type { OperationsDataSource } from './dataSource'

type ApiIncidentStatus = 'escalating' | 'response_active' | 'monitoring' | 'contained'

interface ApiIncident extends Omit<Incident, 'status'> {
  status: ApiIncidentStatus
}

interface ApiDashboardData extends Omit<DashboardData, 'incidents'> {
  incidents: ApiIncident[]
  resources: Resource[]
  facilities: Facility[]
  recommendations: Recommendation[]
}

interface ApiErrorPayload {
  error?: {
    code?: string
    message?: string
  }
}

const statusLabels = {
  escalating: 'Escalating',
  response_active: 'Response active',
  monitoring: 'Monitoring',
  contained: 'Contained',
} satisfies Record<ApiIncidentStatus, IncidentStatus>

export class ApiDataSourceError extends Error {
  constructor(
    message: string,
    readonly status?: number,
    readonly code?: string,
  ) {
    super(message)
    this.name = 'ApiDataSourceError'
  }
}

export class ApiOperationsDataSource implements OperationsDataSource {
  constructor(private readonly baseUrl: string) {}

  async getDashboardData() {
    return this.requestDashboard('/api/dashboard')
  }

  async approveRecommendation(recommendationId: string) {
    return this.requestDashboard(`/api/recommendations/${encodeURIComponent(recommendationId)}/approve`, {
      method: 'POST',
    })
  }

  async dismissRecommendation(recommendationId: string) {
    return this.requestDashboard(`/api/recommendations/${encodeURIComponent(recommendationId)}/dismiss`, {
      method: 'POST',
    })
  }

  private async requestDashboard(path: string, init?: RequestInit) {
    const response = await fetch(`${this.baseUrl}${path}`, {
      ...init,
      headers: { Accept: 'application/json', ...init?.headers },
    })

    if (!response.ok) {
      throw await this.toApiError(response)
    }

    return mapDashboardData(await response.json() as ApiDashboardData)
  }

  private async toApiError(response: Response) {
    const fallbackMessage = `API request failed with status ${response.status}`

    try {
      const payload = await response.json() as ApiErrorPayload
      return new ApiDataSourceError(payload.error?.message ?? fallbackMessage, response.status, payload.error?.code)
    } catch {
      return new ApiDataSourceError(fallbackMessage, response.status)
    }
  }
}

function mapDashboardData(data: ApiDashboardData): DashboardData {
  return {
    ...data,
    incidents: data.incidents.map(mapIncident),
  }
}

function mapIncident(incident: ApiIncident): Incident {
  return {
    ...incident,
    status: statusLabels[incident.status],
  }
}
