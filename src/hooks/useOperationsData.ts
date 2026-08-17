import { useEffect, useState } from 'react'
import type { DashboardData } from '../types/domain'
import type { OperationsDataSource } from '../services/dataSource'

export function useOperationsData(dataSource: OperationsDataSource) {
  const [data, setData] = useState<DashboardData | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [actionPending, setActionPending] = useState<string | null>(null)

  useEffect(() => {
    dataSource.getDashboardData().then(setData).catch(() => setError('Unable to load operations data'))
  }, [dataSource])

  const approveRecommendation = async (recommendationId: string) => {
    setActionPending(recommendationId)
    try {
      setData(await dataSource.approveRecommendation(recommendationId))
    } catch {
      setError('Unable to approve recommendation')
    } finally {
      setActionPending(null)
    }
  }

  const dismissRecommendation = async (recommendationId: string) => {
    setActionPending(recommendationId)
    try {
      setData(await dataSource.dismissRecommendation(recommendationId))
    } catch {
      setError('Unable to dismiss recommendation')
    } finally {
      setActionPending(null)
    }
  }

  return { data, error, actionPending, approveRecommendation, dismissRecommendation }
}
