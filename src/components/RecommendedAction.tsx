import { Check, Sparkles, X } from 'lucide-react'
import type { Recommendation } from '../types/domain'

interface RecommendedActionProps {
  recommendation?: Recommendation
  busy: boolean
  onApprove: (recommendationId: string) => void
  onDismiss: (recommendationId: string) => void
}

export function RecommendedAction({ recommendation, busy, onApprove, onDismiss }: RecommendedActionProps) {
  if (!recommendation) {
    return <section className="recommendation empty-recommendation"><div className="rec-icon"><Sparkles size={19}/></div><div><span>RECOMMENDED ACTION</span><h2>No active recommendations</h2><p>The mock operations engine has no recommendations queued.</p></div></section>
  }

  const isPending = recommendation.status === 'pending'

  return <section className={`recommendation ${recommendation.status}`}><div className="rec-icon"><Sparkles size={19}/></div><div><span>RECOMMENDED ACTION</span><h2>{recommendation.title}</h2><p>{recommendation.detail}</p>{recommendation.statusMessage && <p className="rec-feedback">{recommendation.statusMessage}</p>}<div className="rec-actions"><button type="button" disabled={!isPending || busy} onClick={() => onApprove(recommendation.id)}><Check size={15}/> {busy ? 'Updating' : 'Approve'}</button><button type="button" disabled={!isPending || busy} onClick={() => onDismiss(recommendation.id)}><X size={15}/> Dismiss</button></div></div><span className={`priority ${recommendation.priority}`}>{recommendation.status === 'pending' ? `${recommendation.priority} priority` : recommendation.status}</span></section>
}
