import { useEffect, useState } from 'react'
import type { DashboardData } from '../types/domain'
import type { OperationsDataSource } from '../services/dataSource'

export function useOperationsData(dataSource: OperationsDataSource) {
  const [data, setData] = useState<DashboardData | null>(null)
  const [error, setError] = useState<string | null>(null)
  useEffect(() => { dataSource.getDashboardData().then(setData).catch(() => setError('Unable to load operations data')) }, [dataSource])
  return { data, error }
}
