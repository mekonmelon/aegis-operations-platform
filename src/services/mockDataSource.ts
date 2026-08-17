import { mockDashboardData } from '../data/mockData'
import type { DashboardData } from '../types/domain'
import type { OperationsDataSource } from './dataSource'

export class MockOperationsDataSource implements OperationsDataSource {
  private data: DashboardData = structuredClone(mockDashboardData)

  async getDashboardData() {
    return Promise.resolve(this.snapshot())
  }

  async approveRecommendation(recommendationId: string) {
    const recommendation = this.data.recommendations.find(item => item.id === recommendationId)

    if (!recommendation || recommendation.status !== 'pending') {
      return Promise.resolve(this.snapshot())
    }

    const incident = this.data.incidents.find(item => item.id === recommendation.incidentId)
    const resource = this.data.resources.find(item => item.id === recommendation.resourceId)

    recommendation.status = 'approved'
    recommendation.statusMessage = 'Deployment approved. Resource assignment is reflected in the incident record.'

    if (incident && resource && resource.available > 0) {
      resource.available -= 1

      if (!incident.assignedResourceIds.includes(resource.id)) {
        incident.assignedResourceIds = [...incident.assignedResourceIds, resource.id]
      }

      if (incident.status === 'Escalating') {
        incident.status = 'Response active'
      }
    }

    this.touch()
    return Promise.resolve(this.snapshot())
  }

  async dismissRecommendation(recommendationId: string) {
    const recommendation = this.data.recommendations.find(item => item.id === recommendationId)

    if (recommendation && recommendation.status === 'pending') {
      recommendation.status = 'dismissed'
      recommendation.statusMessage = 'Recommendation dismissed for this demo session.'
      this.touch()
    }

    return Promise.resolve(this.snapshot())
  }

  private snapshot() {
    return structuredClone(this.data)
  }

  private touch() {
    this.data.lastUpdated = new Date().toISOString()
  }
}

export const operationsDataSource: OperationsDataSource = new MockOperationsDataSource()
