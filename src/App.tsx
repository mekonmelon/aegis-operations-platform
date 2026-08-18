import { useMemo, useState } from 'react'
import { Header } from './components/Header'
import { IncidentDetails } from './components/IncidentDetails'
import { IncidentsPanel } from './components/IncidentsPanel'
import { MetricCards } from './components/MetricCards'
import { OperationsMap } from './components/OperationsMap'
import { RecommendedAction } from './components/RecommendedAction'
import { ResourceStatus } from './components/ResourceStatus'
import { useOperationsData } from './hooks/useOperationsData'
import { createOperationsDataSourceConfig } from './services/dataSourceFactory'
import type { IncidentFilters } from './types/domain'
import './styles.css'

const initialFilters: IncidentFilters = { search: '', severity: 'all', kind: 'all', status: 'all' }
const dataSourceConfig = createOperationsDataSourceConfig()

export default function App() {
  const { data, error, actionPending, sourceStatus, sourceMessage, approveRecommendation, dismissRecommendation } = useOperationsData(dataSourceConfig)
  const [userSelectedIncidentId, setUserSelectedIncidentId] = useState<string | null>(null)
  const [filters, setFilters] = useState<IncidentFilters>(initialFilters)

  const filteredIncidents = useMemo(() => {
    if (!data) {
      return []
    }

    const search = filters.search.trim().toLowerCase()

    return data.incidents.filter(incident => {
      const matchesSearch = !search || `${incident.title} ${incident.location}`.toLowerCase().includes(search)
      const matchesSeverity = filters.severity === 'all' || incident.severity === filters.severity
      const matchesKind = filters.kind === 'all' || incident.kind === filters.kind
      const matchesStatus = filters.status === 'all' || incident.status === filters.status

      return matchesSearch && matchesSeverity && matchesKind && matchesStatus
    })
  }, [data, filters])

  if (error) return <main className="state">{error}</main>
  if (!data) return <main className="state">Loading operations center…</main>

  const defaultIncidentId = data.incidents.find(incident => incident.severity === 'critical')?.id ?? data.incidents[0]?.id ?? null
  const selectedIncidentId = userSelectedIncidentId ?? defaultIncidentId
  const selectedIncident = data.incidents.find(incident => incident.id === selectedIncidentId) ?? null

  return <><Header updatedAt={data.lastUpdated} sourceStatus={sourceStatus}/><main>{sourceMessage && <div className="source-alert">{sourceMessage}</div>}<div className="page-title"><div><span>OPERATIONS CENTER</span><h2>Regional Situational Overview</h2></div><p>Monday, August 17, 2026 <i/> Operational Period 04</p></div><MetricCards data={data}/><div className="dashboard-grid"><IncidentsPanel incidents={filteredIncidents} totalIncidents={data.incidents.length} filters={filters} selectedIncidentId={selectedIncidentId} onFiltersChange={setFilters} onSelectIncident={setUserSelectedIncidentId}/><OperationsMap incidents={data.incidents} facilities={data.facilities} selectedIncidentId={selectedIncidentId} onSelectIncident={setUserSelectedIncidentId}/><ResourceStatus resources={data.resources}/><IncidentDetails incident={selectedIncident} facilities={data.facilities} resources={data.resources}/><RecommendedAction recommendation={data.recommendations[0]} busy={actionPending === data.recommendations[0]?.id} onApprove={approveRecommendation} onDismiss={dismissRecommendation}/></div></main><footer><span>AEGIS OPERATIONS NETWORK</span><span>Secure session · Classification: INTERNAL</span></footer></>
}
