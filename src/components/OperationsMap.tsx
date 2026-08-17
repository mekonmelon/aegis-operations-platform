import { Building2, Cross, MapPin, Package, TentTree } from 'lucide-react'
import type { Facility, Incident } from '../types/domain'

const facilityIcons = { hospital: Cross, shelter: TentTree, depot: Package }

interface OperationsMapProps {
  incidents: Incident[]
  facilities: Facility[]
  selectedIncidentId: string | null
  onSelectIncident: (incidentId: string) => void
}

export function OperationsMap({ incidents, facilities, selectedIncidentId, onSelectIncident }: OperationsMapProps) {
  return <section className="panel map-panel"><div className="panel-head map-head"><div><h2>Operational Overview</h2><p>Central response region · Live situational view</p></div><div className="map-tools"><button className="active">All layers</button><button>Incidents</button><button>Resources</button></div></div>
    <div className="map"><div className="region-name north">NORTH RIVER</div><div className="region-name west">WEST RIDGE</div><div className="region-name east">EASTGATE</div><div className="region-name central">CENTRAL DISTRICT</div><div className="river"/><div className="road r1"/><div className="road r2"/><div className="road r3"/>
      {incidents.map(i => { const isSelected = i.id === selectedIncidentId; return <button type="button" className={`map-marker incident-marker ${i.severity} ${isSelected ? 'selected' : ''}`} style={{ left: `${i.coordinates.x}%`, top: `${i.coordinates.y}%` }} title={`${i.title} — ${i.severity}`} key={i.id} onClick={() => onSelectIncident(i.id)} aria-label={`Select ${i.title}`} aria-pressed={isSelected}><span className="pulse"/><MapPin size={17}/>{(i.severity === 'critical' || isSelected) && <em>{i.title}<small>{isSelected ? 'SELECTED' : 'CRITICAL'}</small></em>}</button> })}
      {facilities.map(f => { const Icon = facilityIcons[f.kind]; return <div className={`map-marker facility-marker ${f.status}`} style={{ left: `${f.coordinates.x}%`, top: `${f.coordinates.y}%` }} title={f.name} key={f.id}><Icon size={14}/></div> })}
      <div className="map-legend"><span><i className="critical-dot"/> Critical</span><span><i className="incident-dot"/> Incident</span><span><Building2 size={13}/> Facility</span><span><TentTree size={13}/> Shelter</span></div>
      <div className="scale">2 km</div>
    </div>
  </section>
}
