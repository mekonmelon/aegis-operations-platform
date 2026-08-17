import { ArrowRight, Sparkles } from 'lucide-react'
import type { Recommendation } from '../types/domain'
export function RecommendedAction({ recommendation }: { recommendation: Recommendation }) {
  return <section className="recommendation"><div className="rec-icon"><Sparkles size={19}/></div><div><span>RECOMMENDED ACTION</span><h2>{recommendation.title}</h2><p>{recommendation.detail}</p><button>{recommendation.actionLabel} <ArrowRight size={15}/></button></div><span className={`priority ${recommendation.priority}`}>{recommendation.priority} priority</span></section>
}
