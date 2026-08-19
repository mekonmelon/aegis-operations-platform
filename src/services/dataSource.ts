import type { DashboardData, IncidentDeclarationMatch } from '../types/domain'

export interface OperationsDataSource {
  getDashboardData(): Promise<DashboardData>
  getRelatedDeclarations(incidentId: string): Promise<IncidentDeclarationMatch[]>
  approveRecommendation(recommendationId: string): Promise<DashboardData>
  dismissRecommendation(recommendationId: string): Promise<DashboardData>
}

export type DataMode = 'api' | 'mock'
export type DataSourceStatus = 'api' | 'mock' | 'fallback'

export interface OperationsDataSourceConfig {
  requestedMode: DataMode
  primary: OperationsDataSource
  fallback?: OperationsDataSource
}
