import { AlertTriangle, Building2, RadioTower, Truck } from 'lucide-react'
import type { DashboardData } from '../types/domain'

export function MetricCards({ data }: { data: DashboardData }) {
  const available = data.resources.filter(r => r.kind === 'teams' || r.kind === 'vehicles').reduce((a, r) => a + r.available, 0)
  const metrics = [
    { label: 'Active Incidents', value: data.incidents.length, note: '+1 in last hour', icon: RadioTower, tone: 'blue' },
    { label: 'Critical Incidents', value: data.incidents.filter(i => i.severity === 'critical').length, note: 'Requires attention', icon: AlertTriangle, tone: 'red' },
    { label: 'Available Resources', value: available, note: '72% ready', icon: Truck, tone: 'green' },
    { label: 'Facilities at Risk', value: data.facilities.filter(f => f.status === 'at-risk').length, note: `${data.facilities.length} monitored`, icon: Building2, tone: 'amber' },
  ]
  return <section className="metrics">{metrics.map(m => <article className={`metric ${m.tone}`} key={m.label}><div><span>{m.label}</span><strong>{m.value}</strong><small>{m.note}</small></div><m.icon size={21}/></article>)}</section>
}
