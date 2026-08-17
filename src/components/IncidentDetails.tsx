import { ClipboardList, MapPinned } from 'lucide-react'
import type { Facility, Incident, Resource } from '../types/domain'

interface IncidentDetailsProps {
  incident: Incident | null
  facilities: Facility[]
  resources: Resource[]
}

export function IncidentDetails({ incident, facilities, resources }: IncidentDetailsProps) {
  const affectedFacilities = facilities.filter(facility => incident?.affectedFacilityIds.includes(facility.id))
  const assignedResources = resources.filter(resource => incident?.assignedResourceIds.includes(resource.id))

  return <section className="panel incident-details"><div className="panel-head"><div><h2>Incident Details</h2><p>Selected operational record</p></div><span className="detail-indicator"><ClipboardList size={14}/> ACTIVE</span></div>
    {incident ? <div className="detail-body">
      <div className="detail-title"><div><span>{incident.kind}</span><h3>{incident.title}</h3></div><span className={`severity ${incident.severity}`}>{incident.severity}</span></div>
      <p className="detail-description">{incident.description}</p>
      <div className="detail-grid">
        <div><span>Status</span><strong>{incident.status}</strong></div>
        <div><span>Location</span><strong>{incident.location}</strong></div>
        <div><span>Reported</span><strong>{new Intl.DateTimeFormat('en', { hour: 'numeric', minute: '2-digit' }).format(new Date(incident.reportedAt))}</strong></div>
        <div><span>Incident ID</span><strong>{incident.id}</strong></div>
      </div>
      <div className="detail-section"><h4>Affected Facilities</h4>{affectedFacilities.length > 0 ? <ul>{affectedFacilities.map(facility => <li key={facility.id}><MapPinned size={13}/><span>{facility.name}</span><b>{facility.status}</b></li>)}</ul> : <p>No facilities currently linked.</p>}</div>
      <div className="detail-section"><h4>Assigned Resources</h4>{assignedResources.length > 0 ? <ul>{assignedResources.map(resource => <li key={resource.id}><span>{resource.label}</span><b>{resource.available} {resource.unit} ready</b></li>)}</ul> : <p>No resources assigned yet.</p>}</div>
    </div> : <div className="empty-state">Select an incident from the queue or map to inspect operational details.</div>}
  </section>
}
