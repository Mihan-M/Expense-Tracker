import React, { useEffect, useState } from 'react'
import { TrendingUp, TrendingDown, Wallet } from 'lucide-react'
import Layout from '../components/Layout'
import StatCard from '../components/StatCard'
import TransactionList from '../components/TransactionList'
import CategoryBreakdownChart from '../components/CategoryBreakdownChart'
import { dashboardApi } from '../api/services'
import { formatCurrency } from '../utils/format'

export default function Dashboard() {
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true
    dashboardApi
      .get()
      .then(({ data }) => {
        if (active) setData(data)
      })
      .catch(() => {
        if (active) setError('Could not load dashboard data.')
      })
      .finally(() => {
        if (active) setLoading(false)
      })
    return () => {
      active = false
    }
  }, [])

  const balancePositive = Number(data?.balance) >= 0

  return (
    <Layout title="Dashboard">
      {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2.5 mb-5">{error}</p>}

      {loading || !data ? (
        <DashboardSkeleton />
      ) : (
        <div className="space-y-6">
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
      <div className="grid grid-cols-1 lg:grid-cols-5 gap-6">
        <div className="lg:col-span-3 h-64 bg-slate-100 rounded-2xl" />
        <div className="lg:col-span-2 h-64 bg-slate-100 rounded-2xl" />
      </div>
    </div>
  )
}
