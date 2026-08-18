import type { DashboardData } from '../types/domain'

export interface OperationsDataSource {
  getDashboardData(): Promise<DashboardData>
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
