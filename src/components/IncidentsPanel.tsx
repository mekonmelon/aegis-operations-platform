import { Flame, Waves, Zap, TrafficCone, ChevronRight } from 'lucide-react'
import type { Incident, IncidentFilters, IncidentKind, IncidentStatus, Severity } from '../types/domain'

const icons = { flood: Waves, wildfire: Flame, outage: Zap, road: TrafficCone } satisfies Record<IncidentKind, typeof Waves>
const severities: Array<Severity | 'all'> = ['all', 'critical', 'high', 'moderate', 'low']
const kinds: Array<IncidentKind | 'all'> = ['all', 'flood', 'wildfire', 'outage', 'road']
const statuses: Array<IncidentStatus | 'all'> = ['all', 'Escalating', 'Response active', 'Monitoring', 'Contained']

interface IncidentsPanelProps {
  incidents: Incident[]
  totalIncidents: number
  filters: IncidentFilters
  selectedIncidentId: string | null
  onFiltersChange: (filters: IncidentFilters) => void
  onSelectIncident: (incidentId: string) => void
}

export function IncidentsPanel({ incidents, totalIncidents, filters, selectedIncidentId, onFiltersChange, onSelectIncident }: IncidentsPanelProps) {
  const updateFilter = <Key extends keyof IncidentFilters>(key: Key, value: IncidentFilters[Key]) => {
    onFiltersChange({ ...filters, [key]: value })
  }

  return <section className="panel incidents"><div className="panel-head"><div><h2>Active Incidents</h2><p>Live incident queue</p></div><button>View all <ChevronRight size={15}/></button></div>
    <div className="incident-filters">
      <label><span>Search</span><input value={filters.search} onChange={event => updateFilter('search', event.target.value)} placeholder="Name or location"/></label>
      <div className="filter-row">
        <label><span>Severity</span><select value={filters.severity} onChange={event => updateFilter('severity', event.target.value as IncidentFilters['severity'])}>{severities.map(item => <option key={item} value={item}>{item === 'all' ? 'All' : item}</option>)}</select></label>
        <label><span>Type</span><select value={filters.kind} onChange={event => updateFilter('kind', event.target.value as IncidentFilters['kind'])}>{kinds.map(item => <option key={item} value={item}>{item === 'all' ? 'All' : item}</option>)}</select></label>
        <label><span>Status</span><select value={filters.status} onChange={event => updateFilter('status', event.target.value as IncidentFilters['status'])}>{statuses.map(item => <option key={item} value={item}>{item === 'all' ? 'All' : item}</option>)}</select></label>
      </div>
      <p>{incidents.length} of {totalIncidents} incidents shown</p>
    </div>
    <div className="incident-list">{incidents.length > 0 ? incidents.map(item => { const Icon = icons[item.kind]; const isSelected = item.id === selectedIncidentId; return <button type="button" className={`incident ${isSelected ? 'selected' : ''}`} key={item.id} onClick={() => onSelectIncident(item.id)} aria-pressed={isSelected}><span className={`incident-icon ${item.severity}`}><Icon size={18}/></span><span className="incident-main"><span className="incident-title"><h3>{item.title}</h3><span className={`severity ${item.severity}`}>{item.severity}</span></span><span className="incident-location">{item.location}</span><span className="incident-meta"><span>{item.status}</span><time>{new Intl.DateTimeFormat('en', { hour: 'numeric', minute: '2-digit' }).format(new Date(item.reportedAt))}</time><b>{item.id}</b></span></span></button> }) : <div className="empty-state">No incidents match the current filters.</div>}</div>
  </section>
}
