import React from 'react'
import { ArrowDownLeft, ArrowUpRight } from 'lucide-react'
import { formatCurrency, formatDate, categoryLabel } from '../utils/format'
import EmptyState from './EmptyState'
import { Receipt } from 'lucide-react'

export default function TransactionList({ transactions }) {
  if (!transactions || transactions.length === 0) {
    return <EmptyState icon={Receipt} title="No transactions yet" description="Add an expense or income record to see it here." />
  }

  return (
    <div className="divide-y divide-slate-100">
      {transactions.map((tx) => {
        const isIncome = tx.type === 'INCOME'
        return (
          <div key={`${tx.type}-${tx.id}`} className="flex items-center gap-3 py-3.5">
            <div
              className={`w-9 h-9 rounded-full flex items-center justify-center shrink-0 ${
                isIncome ? 'bg-emerald-50 text-emerald-600' : 'bg-red-50 text-red-500'
              }`}
            >
              {isIncome ? <ArrowDownLeft size={16} /> : <ArrowUpRight size={16} />}
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-sm font-medium text-slate-800 truncate">{tx.title}</p>
              <p className="text-xs text-slate-400">
                {formatDate(tx.date)}
                {tx.category ? ` · ${categoryLabel(tx.category)}` : ''}
              </p>
            </div>
            <span className={`text-sm font-semibold shrink-0 ${isIncome ? 'text-emerald-600' : 'text-red-500'}`}>
              {isIncome ? '+' : '-'}
              {formatCurrency(tx.amount)}
            </span>
          </div>
        )
      })}
    </div>
  )
}
