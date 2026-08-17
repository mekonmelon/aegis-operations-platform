import type { DashboardData } from '../types/domain'

export interface OperationsDataSource {
  getDashboardData(): Promise<DashboardData>
  approveRecommendation(recommendationId: string): Promise<DashboardData>
  dismissRecommendation(recommendationId: string): Promise<DashboardData>
}
