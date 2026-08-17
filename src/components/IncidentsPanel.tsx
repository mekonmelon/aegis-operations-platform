import { Flame, Waves, Zap, TrafficCone, ChevronRight } from 'lucide-react'
import type { Incident, IncidentKind } from '../types/domain'

const icons = { flood: Waves, wildfire: Flame, outage: Zap, road: TrafficCone } satisfies Record<IncidentKind, typeof Waves>
export function IncidentsPanel({ incidents }: { incidents: Incident[] }) {
  return <section className="panel incidents"><div className="panel-head"><div><h2>Active Incidents</h2><p>Live incident queue</p></div><button>View all <ChevronRight size={15}/></button></div>
    <div className="incident-list">{incidents.map(item => { const Icon = icons[item.kind]; return <article className="incident" key={item.id}><span className={`incident-icon ${item.severity}`}><Icon size={18}/></span><div className="incident-main"><div className="incident-title"><h3>{item.title}</h3><span className={`severity ${item.severity}`}>{item.severity}</span></div><p>{item.location}</p><div className="incident-meta"><span>{item.status}</span><time>{new Intl.DateTimeFormat('en', { hour: 'numeric', minute: '2-digit' }).format(new Date(item.reportedAt))}</time><b>{item.id}</b></div></div></article> })}</div>
  </section>
}
