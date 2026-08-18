import { useEffect, useRef, useState } from 'react'
import type { DashboardData } from '../types/domain'
import type { DataSourceStatus, OperationsDataSource, OperationsDataSourceConfig } from '../services/dataSource'

export function useOperationsData(dataSourceConfig: OperationsDataSourceConfig) {
  const [data, setData] = useState<DashboardData | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [actionPending, setActionPending] = useState<string | null>(null)
  const [sourceStatus, setSourceStatus] = useState<DataSourceStatus>(dataSourceConfig.requestedMode === 'api' ? 'api' : 'mock')
  const [sourceMessage, setSourceMessage] = useState<string | null>(null)
  const activeDataSource = useRef<OperationsDataSource>(dataSourceConfig.primary)

  useEffect(() => {
    let isMounted = true

    async function loadDashboard() {
      try {
        const dashboardData = await dataSourceConfig.primary.getDashboardData()

        if (!isMounted) {
          return
        }

        activeDataSource.current = dataSourceConfig.primary
        setSourceStatus(dataSourceConfig.requestedMode === 'api' ? 'api' : 'mock')
        setSourceMessage(null)
        setData(dashboardData)
      } catch {
        if (dataSourceConfig.requestedMode === 'api' && dataSourceConfig.fallback) {
          try {
            const fallbackData = await dataSourceConfig.fallback.getDashboardData()

            if (!isMounted) {
              return
            }

            activeDataSource.current = dataSourceConfig.fallback
            setSourceStatus('fallback')
            setSourceMessage('Backend unavailable. Using mock demo data for this session.')
            setData(fallbackData)
          } catch {
            if (isMounted) {
              setError('Unable to load operations data')
            }
          }
        } else if (isMounted) {
          setError('Unable to load operations data')
        }
      }
    }

    loadDashboard()

    return () => {
      isMounted = false
    }
  }, [dataSourceConfig])

  const approveRecommendation = async (recommendationId: string) => {
    setActionPending(recommendationId)
    try {
      setData(await activeDataSource.current.approveRecommendation(recommendationId))
      setError(null)
    } catch (approvalError) {
      setError(errorMessage(approvalError, 'Unable to approve recommendation'))
    } finally {
      setActionPending(null)
    }
  }

  const dismissRecommendation = async (recommendationId: string) => {
    setActionPending(recommendationId)
    try {
      setData(await activeDataSource.current.dismissRecommendation(recommendationId))
      setError(null)
    } catch (dismissError) {
      setError(errorMessage(dismissError, 'Unable to dismiss recommendation'))
    } finally {
      setActionPending(null)
    }
  }

  return { data, error, actionPending, sourceStatus, sourceMessage, approveRecommendation, dismissRecommendation }
}

function errorMessage(error: unknown, fallback: string) {
  return error instanceof Error ? error.message : fallback
}
