import React from 'react'
import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts'
import { PieChart as PieIcon } from 'lucide-react'
import { formatCurrency, categoryLabel, CATEGORY_COLORS } from '../utils/format'
import EmptyState from './EmptyState'

export default function CategoryBreakdownChart({ data }) {
  const chartData = (data || []).filter((d) => Number(d.totalAmount) > 0)

  if (chartData.length === 0) {
    return <EmptyState icon={PieIcon} title="No expenses yet" description="Your category breakdown will appear here once you add expenses." />
  }

  return (
    <div className="flex flex-col sm:flex-row items-center gap-6">
      <div className="w-44 h-44 shrink-0">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={chartData}
              dataKey="totalAmount"
              nameKey="category"
              innerRadius={48}
              outerRadius={72}
              paddingAngle={2}
              stroke="none"
            >
              {chartData.map((entry) => (
                <Cell key={entry.category} fill={CATEGORY_COLORS[entry.category] || '#94a3b8'} />
              ))}
            </Pie>
            <Tooltip
              formatter={(value, name) => [formatCurrency(value), categoryLabel(name)]}
              contentStyle={{ borderRadius: 12, border: '1px solid #e2e8f0', fontSize: 13 }}
            />
          </PieChart>
        </ResponsiveContainer>
      </div>
      <div className="flex-1 w-full space-y-2.5">
        {chartData
          .sort((a, b) => b.totalAmount - a.totalAmount)
          .map((entry) => (
            <div key={entry.category} className="flex items-center justify-between text-sm">
              <div className="flex items-center gap-2 min-w-0">
                <span
                  className="w-2.5 h-2.5 rounded-full shrink-0"
                  style={{ backgroundColor: CATEGORY_COLORS[entry.category] || '#94a3b8' }}
                />
                <span className="text-slate-600 truncate">{categoryLabel(entry.category)}</span>
              </div>
              <div className="flex items-center gap-3 shrink-0">
                <span className="text-slate-400 text-xs">{entry.percentage?.toFixed(1)}%</span>
                <span className="font-medium text-slate-800">{formatCurrency(entry.totalAmount)}</span>
              </div>
            </div>
          ))}
      </div>
    </div>
  )
}
