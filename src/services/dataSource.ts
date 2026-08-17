import type { DashboardData } from '../types/domain'

export interface OperationsDataSource { getDashboardData(): Promise<DashboardData> }
