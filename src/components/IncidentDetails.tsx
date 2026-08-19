import { ClipboardList, MapPinned } from 'lucide-react'
import type { Facility, Incident, IncidentDeclarationMatch, Resource } from '../types/domain'

interface IncidentDetailsProps {
  incident: Incident | null
  facilities: Facility[]
  resources: Resource[]
  relatedDeclarations: IncidentDeclarationMatch[]
}

export function IncidentDetails({ incident, facilities, resources, relatedDeclarations }: IncidentDetailsProps) {
  const affectedFacilities = facilities.filter(facility => incident?.affectedFacilityIds.includes(facility.id))
  const assignedResources = resources.filter(resource => incident?.assignedResourceIds.includes(resource.id))
  const sourceUpdatedAt = incident?.sourceUpdatedAt ?? incident?.reportedAt

  return <section className="panel incident-details"><div className="panel-head"><div><h2>Incident Details</h2><p>Selected operational record</p></div><span className="detail-indicator"><ClipboardList size={14}/> ACTIVE</span></div>
    {incident ? <div className="detail-body">
      <div className="detail-title"><div><span>{incident.kind}</span><h3>{incident.title}</h3></div><div className="detail-badges"><span className={`source-badge ${incident.source}`}>{incident.source.toUpperCase()}</span><span className={`severity ${incident.severity}`}>{incident.severity}</span></div></div>
      <p className="detail-description">{incident.description}</p>
      <div className="detail-grid">
        <div><span>Status</span><strong>{incident.status}</strong></div>
        <div><span>Location</span><strong>{incident.location}</strong></div>
        <div><span>Reported</span><strong>{new Intl.DateTimeFormat('en', { hour: 'numeric', minute: '2-digit' }).format(new Date(incident.reportedAt))}</strong></div>
        <div><span>Incident ID</span><strong>{incident.id}</strong></div>
        <div><span>Source Updated</span><strong>{sourceUpdatedAt ? new Intl.DateTimeFormat('en', { month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' }).format(new Date(sourceUpdatedAt)) : 'Unknown'}</strong></div>
        <div><span>Ingested</span><strong>{incident.ingestedAt ? new Intl.DateTimeFormat('en', { month: 'short', day: 'numeric', hour: 'numeric', minute: '2-digit' }).format(new Date(incident.ingestedAt)) : 'Demo seed'}</strong></div>
        <div><span>Source ID</span><strong>{incident.sourceId ?? incident.id}</strong></div>
        <div><span>Source Link</span><strong>{incident.sourceUrl ? <a href={incident.sourceUrl} target="_blank" rel="noreferrer">Open record</a> : 'Local demo'}</strong></div>
      </div>
      <div className="detail-section"><h4>Affected Facilities</h4>{affectedFacilities.length > 0 ? <ul>{affectedFacilities.map(facility => <li key={facility.id}><MapPinned size={13}/><span>{facility.name}</span><b>{facility.status}</b></li>)}</ul> : <p>No facilities currently linked.</p>}</div>
      <div className="detail-section"><h4>Assigned Resources</h4>{assignedResources.length > 0 ? <ul>{assignedResources.map(resource => <li key={resource.id}><span>{resource.label}</span><b>{resource.available} {resource.unit} ready</b></li>)}</ul> : <p>No resources assigned yet.</p>}</div>
      <div className="detail-section fema-links"><h4>Related FEMA Declarations</h4>{relatedDeclarations.length > 0 ? <ul>{relatedDeclarations.map(match => <li key={match.declaration.id}><span><b>{match.declaration.id}</b>{match.declaration.title}<small>{match.declaration.source.toUpperCase()} · {match.reasons.map(reason => reason.replaceAll('_', ' ')).join(', ')}</small></span><strong>{Math.round(match.confidence * 100)}%</strong></li>)}</ul> : <p>No related FEMA declaration identified.</p>}</div>
    </div> : <div className="empty-state">Select an incident from the queue or map to inspect operational details.</div>}
  </section>
}
