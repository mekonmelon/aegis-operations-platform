import { Ambulance, Boxes, HeartPulse, UsersRound } from 'lucide-react'
import type { Resource, ResourceKind } from '../types/domain'
const icons = { teams: UsersRound, vehicles: Ambulance, medical: HeartPulse, supplies: Boxes } satisfies Record<ResourceKind, typeof UsersRound>
export function ResourceStatus({ resources }: { resources: Resource[] }) {
  return <section className="panel resources"><div className="panel-head"><div><h2>Resource Status</h2><p>Regional availability</p></div><span className="live"><i/> LIVE</span></div><div className="resource-list">{resources.map(r => { const Icon = icons[r.kind]; const pct = r.kind === 'supplies' ? r.available : Math.round(r.available / r.total * 100); return <article key={r.id}><span className="resource-icon"><Icon size={17}/></span><div className="resource-data"><div><h3>{r.label}</h3><strong>{r.available}<small> / {r.kind === 'supplies' ? '100' : r.total} {r.unit}</small></strong></div><div className="bar"><i style={{ width: `${pct}%` }}/></div></div></article>})}</div></section>
}
