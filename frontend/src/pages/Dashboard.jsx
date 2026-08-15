import React, { useEffect, useState } from 'react'
import { TrendingUp, TrendingDown, Wallet, Calendar, Tag } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import TransactionList from '../components/TransactionList'
import CategoryBreakdownChart from '../components/CategoryBreakdownChart'
import { dashboardApi } from '../api/services'
import { formatCurrency, categoryLabel } from '../utils/format'

const MONTHS = [
  { value: 1, label: 'January' },
  { value: 2, label: 'February' },
  { value: 3, label: 'March' },
  { value: 4, label: 'April' },
  { value: 5, label: 'May' },
  { value: 6, label: 'June' },
  { value: 7, label: 'July' },
  { value: 8, label: 'August' },
  { value: 9, label: 'September' },
  { value: 10, label: 'October' },
  { value: 11, label: 'November' },
  { value: 12, label: 'December' }
]

const currentYear = new Date().getFullYear()
const YEARS = Array.from({ length: 6 }, (_, i) => currentYear - 3 + i)

export default function Dashboard() {
  const [selectedYear, setSelectedYear] = useState(new Date().getFullYear())
  const [selectedMonth, setSelectedMonth] = useState(new Date().getMonth() + 1)

  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    setLoading(true)
    dashboardApi
      .get({ year: selectedYear, month: selectedMonth })
      .then(({ data }) => {
        if (active) {
          setData(data)
          setError('')
        }
      })
      .catch((err) => {
        if (active) {
          if (err.response?.status === 403) {
            setError("You don't have access to this page")
          } else {
            setError('Could not load dashboard data.')
          }
        }
      })

      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
  }, [selectedYear, selectedMonth])

  const balancePositive = Number(data?.balance) >= 0
  const selectedMonthName = MONTHS.find((m) => m.value === Number(selectedMonth))?.label || ''
  const highestCat = data?.highestExpenseCategory
  const highestCategoryDisplay = !highestCat || highestCat === 'N/A' ? 'N/A' : categoryLabel(highestCat)

  return (
    <Layout title="Dashboard">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
        <h2 className="text-base font-semibold text-slate-800">
          Overview ({selectedMonthName} {selectedYear})
        </h2>
        <div className="flex items-center gap-3">
          <select
            value={selectedMonth}
            onChange={(e) => setSelectedMonth(Number(e.target.value))}
            className="bg-white border border-slate-300 text-slate-700 text-sm rounded-xl px-3 py-2 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 shadow-sm"
          >
            {MONTHS.map((m) => (
              <option key={m.value} value={m.value}>
                {m.label}
              </option>
            ))}
          </select>
          <select
            value={selectedYear}
            onChange={(e) => setSelectedYear(Number(e.target.value))}
            className="bg-white border border-slate-300 text-slate-700 text-sm rounded-xl px-3 py-2 focus:outline-none focus:ring-2 focus:ring-brand-500/20 focus:border-brand-500 shadow-sm"
          >
            {YEARS.map((y) => (
              <option key={y} value={y}>
                {y}
              </option>
            ))}
          </select>
        </div>
      </div>

      {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2.5 mb-5">{error}</p>}

      {loading || !data ? (
        <DashboardSkeleton />
      ) : (
        <div className="space-y-6">
          <div>
            <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-400 mb-3">All-Time Totals</h3>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <StatCard label="Total income" value={formatCurrency(data.totalIncome)} icon={TrendingUp} tone="green" />
              <StatCard label="Total expenses" value={formatCurrency(data.totalExpenses)} icon={TrendingDown} tone="red" />
              <StatCard
                label="Current balance"
                value={formatCurrency(data.balance)}
                icon={Wallet}
                tone={balancePositive ? 'brand' : 'red'}
              />
            </div>
          </div>

          <div>
            <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-400 mb-3">
              {selectedMonthName} {selectedYear} Figures
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <StatCard label="Monthly income" value={formatCurrency(data.monthlyIncomeTotal)} icon={Calendar} tone="green" />
              <StatCard label="Monthly expenses" value={formatCurrency(data.monthlyExpenseTotal)} icon={Calendar} tone="red" />
              <StatCard label="Top expense category" value={highestCategoryDisplay} icon={Tag} tone="brand" />
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
            <div className="lg:col-span-3 bg-white rounded-2xl border border-slate-200 shadow-card p-6">
              <h2 className="text-sm font-semibold text-slate-700 mb-5">Spending by category</h2>
              <CategoryBreakdownChart data={data.categoryBreakdown} />
            </div>

            <div className="lg:col-span-2 bg-white rounded-2xl border border-slate-200 shadow-card p-6">
              <h2 className="text-sm font-semibold text-slate-700 mb-1">Recent transactions</h2>
              <TransactionList transactions={data.recentTransactions} />
            </div>
          </div>
        </div>
      )}
    </Layout>
  )
}

function DashboardSkeleton() {
  return (
    <div className="space-y-6 animate-pulse">
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {[0, 1, 2].map((i) => (
          <div key={i} className="h-24 bg-slate-100 rounded-2xl" />
        ))}
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        {[0, 1, 2].map((i) => (
          <div key={i} className="h-24 bg-slate-100 rounded-2xl" />
        ))}
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
        <div className="lg:col-span-3 h-64 bg-slate-100 rounded-2xl" />
        <div className="lg:col-span-2 h-64 bg-slate-100 rounded-2xl" />
      </div>
    </div>
  )
}
