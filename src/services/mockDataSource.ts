import { mockDashboardData } from '../data/mockData'
import type { OperationsDataSource } from './dataSource'

export class MockOperationsDataSource implements OperationsDataSource {
  async getDashboardData() { return Promise.resolve(mockDashboardData) }
}

export const operationsDataSource: OperationsDataSource = new MockOperationsDataSource()
