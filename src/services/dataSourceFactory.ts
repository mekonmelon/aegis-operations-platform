import { ApiOperationsDataSource } from './apiDataSource'
import type { DataMode, OperationsDataSourceConfig } from './dataSource'
import { MockOperationsDataSource } from './mockDataSource'

const defaultApiBaseUrl = 'http://localhost:8080'

export function createOperationsDataSourceConfig(): OperationsDataSourceConfig {
  const requestedMode = getRequestedMode()
  const mockDataSource = new MockOperationsDataSource()

  if (requestedMode === 'mock') {
    return {
      requestedMode,
      primary: mockDataSource,
    }
  }

  return {
    requestedMode,
    primary: new ApiOperationsDataSource(getApiBaseUrl()),
    fallback: mockDataSource,
  }
}

function getRequestedMode(): DataMode {
  const mode = import.meta.env.VITE_DATA_MODE

  if (mode === 'mock' || mode === 'api') {
    return mode
  }

  return 'api'
}

function getApiBaseUrl() {
  return (import.meta.env.VITE_API_BASE_URL || defaultApiBaseUrl).replace(/\/$/, '')
}
