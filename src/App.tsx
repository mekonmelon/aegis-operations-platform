import { Header } from './components/Header'
import { IncidentsPanel } from './components/IncidentsPanel'
import { MetricCards } from './components/MetricCards'
import { OperationsMap } from './components/OperationsMap'
import { RecommendedAction } from './components/RecommendedAction'
import { ResourceStatus } from './components/ResourceStatus'
import { useOperationsData } from './hooks/useOperationsData'
import { operationsDataSource } from './services/mockDataSource'
import './styles.css'

export default function App() {
  const { data, error } = useOperationsData(operationsDataSource)
  if (error) return <main className="state">{error}</main>
  if (!data) return <main className="state">Loading operations center…</main>
  return <><Header updatedAt={data.lastUpdated}/><main><div className="page-title"><div><span>OPERATIONS CENTER</span><h2>Regional Situational Overview</h2></div><p>Monday, August 17, 2026 <i/> Operational Period 04</p></div><MetricCards data={data}/><div className="dashboard-grid"><IncidentsPanel incidents={data.incidents}/><OperationsMap incidents={data.incidents} facilities={data.facilities}/><ResourceStatus resources={data.resources}/><RecommendedAction recommendation={data.recommendations[0]}/></div></main><footer><span>AEGIS OPERATIONS NETWORK</span><span>Secure session · Classification: INTERNAL</span></footer></>
}
