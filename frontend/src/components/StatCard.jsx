import React from 'react'

export default function StatCard({ label, value, icon: Icon, tone = 'brand' }) {
  const tones = {
    brand: 'bg-brand-50 text-brand-600',
    green: 'bg-emerald-50 text-emerald-600',
    red: 'bg-red-50 text-red-500',
    slate: 'bg-slate-100 text-slate-600'
  }

  return (
    <div className="bg-white rounded-2xl border border-slate-200 shadow-card p-5 flex items-start justify-between">
      <div>
        <p className="text-sm text-slate-500 font-medium">{label}</p>
        <p className="mt-2 text-2xl font-semibold text-slate-900 tracking-tight">{value}</p>
      </div>
      {Icon && (
        <div className={`w-11 h-11 rounded-xl flex items-center justify-center shrink-0 ${tones[tone]}`}>
          <Icon size={20} strokeWidth={2} />
        </div>
      )}
    </div>
  )
}
