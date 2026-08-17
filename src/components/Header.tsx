import { Bell, Radio, ShieldCheck } from 'lucide-react'

export function Header({ updatedAt }: { updatedAt: string }) {
  const time = new Intl.DateTimeFormat('en', { hour: '2-digit', minute: '2-digit', hour12: false, timeZone: 'UTC' }).format(new Date(updatedAt))
  return <header className="header">
    <div className="brand"><span className="brand-mark"><ShieldCheck size={24}/></span><div><h1>Aegis</h1><p>Real-Time Crisis Operations Platform</p></div></div>
    <div className="header-actions"><span className="sync"><i/> Systems operational <b>·</b> Updated {time} UTC</span><span className="demo"><Radio size={13}/> DEMO DATA</span><button className="icon-button" aria-label="Notifications"><Bell size={18}/><i/></button><span className="avatar">OC</span></div>
  </header>
}
