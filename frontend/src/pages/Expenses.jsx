import React, { useEffect, useState, useCallback } from 'react'
import { Plus, Pencil, Trash2, Search, Receipt } from 'lucide-react'
import Layout from '../components/Layout'
import ExpenseForm from '../components/ExpenseForm'
import ConfirmDialog from '../components/ConfirmDialog'
import EmptyState from '../components/EmptyState'
import { expenseApi } from '../api/services'
import { formatCurrency, formatDate, categoryLabel, EXPENSE_CATEGORIES, CATEGORY_COLORS } from '../utils/format'

export default function Expenses() {
  const [expenses, setExpenses] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [deleting, setDeleting] = useState(null)

  const [filters, setFilters] = useState({ category: '', startDate: '', endDate: '', search: '' })

  const loadExpenses = useCallback(() => {
    setLoading(true)
    const activeFilters = Object.fromEntries(Object.entries(filters).filter(([, v]) => v))
    expenseApi
      .list(activeFilters)
      .then(({ data }) => setExpenses(data))
      .catch(() => setError('Could not load expenses.'))
      .finally(() => setLoading(false))
  }, [filters])

  useEffect(() => {
    const timer = setTimeout(loadExpenses, filters.search ? 350 : 0)
    return () => clearTimeout(timer)
  }, [loadExpenses, filters.search])

  const handleFilterChange = (e) => setFilters((prev) => ({ ...prev, [e.target.name]: e.target.value }))

  const handleCreate = async (payload) => {
    await expenseApi.create(payload)
    setShowForm(false)
    loadExpenses()
  }

  const handleUpdate = async (payload) => {
    await expenseApi.update(editing.id, payload)
    setEditing(null)
    loadExpenses()
  }

  const handleDelete = async () => {
    await expenseApi.remove(deleting.id)
    setDeleting(null)
    loadExpenses()
  }

  const hasActiveFilters = Object.values(filters).some(Boolean)

  return (
    <Layout
      title="Expenses"
      actions={
        <button
          onClick={() => setShowForm(true)}
          className="hidden sm:flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-brand-500 text-white text-sm font-medium hover:bg-brand-600 transition-colors"
        >
          <Plus size={16} /> Add expense
        </button>
      }
    >
      <button
        onClick={() => setShowForm(true)}
        className="sm:hidden mb-4 flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-brand-500 text-white text-sm font-medium hover:bg-brand-600 transition-colors"
      >
        <Plus size={16} /> Add expense
      </button>

      <div className="bg-white rounded-2xl border border-slate-200 shadow-card p-4 mb-5 flex flex-wrap gap-3">
        <div className="relative flex-1 min-w-[180px]">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            name="search"
            value={filters.search}
            onChange={handleFilterChange}
            placeholder="Search by title…"
            className="w-full pl-9 pr-3 py-2 text-sm rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent"
          />
        </div>
        <select
          name="category"
          value={filters.category}
          onChange={handleFilterChange}
          className="px-3 py-2 text-sm rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-400"
        >
          <option value="">All categories</option>
          {EXPENSE_CATEGORIES.map((cat) => (
            <option key={cat} value={cat}>
              {categoryLabel(cat)}
            </option>
          ))}
        </select>
        <input
          type="date"
          name="startDate"
          value={filters.startDate}
          onChange={handleFilterChange}
          className="px-3 py-2 text-sm rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-400"
        />
        <input
          type="date"
          name="endDate"
          value={filters.endDate}
          onChange={handleFilterChange}
          className="px-3 py-2 text-sm rounded-lg border border-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-400"
        />
        {hasActiveFilters && (
          <button
            onClick={() => setFilters({ category: '', startDate: '', endDate: '', search: '' })}
            className="text-sm text-slate-500 hover:text-slate-800 px-2"
          >
            Clear
          </button>
        )}
      </div>

      {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2.5 mb-4">{error}</p>}

      <div className="bg-white rounded-2xl border border-slate-200 shadow-card overflow-hidden">
        {loading ? (
          <div className="p-6 space-y-3 animate-pulse">
            {[0, 1, 2, 3].map((i) => (
              <div key={i} className="h-12 bg-slate-100 rounded-lg" />
            ))}
          </div>
        ) : expenses.length === 0 ? (
          <EmptyState
            icon={Receipt}
            title={hasActiveFilters ? 'No matching expenses' : 'No expenses yet'}
            description={
              hasActiveFilters ? 'Try adjusting your filters.' : 'Add your first expense to start tracking.'
            }
          />
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-slate-500 text-xs uppercase tracking-wide">
              <tr>
                <th className="text-left px-5 py-3 font-medium">Title</th>
                <th className="text-left px-5 py-3 font-medium hidden sm:table-cell">Category</th>
                <th className="text-left px-5 py-3 font-medium">Date</th>
                <th className="text-right px-5 py-3 font-medium">Amount</th>
                <th className="px-5 py-3"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {expenses.map((exp) => (
                <tr key={exp.id} className="hover:bg-slate-50/70 transition-colors">
                  <td className="px-5 py-3.5">
                    <p className="font-medium text-slate-800">{exp.title}</p>
                    {exp.note && <p className="text-xs text-slate-400 mt-0.5">{exp.note}</p>}
                    <span
                      className="sm:hidden inline-flex items-center gap-1.5 mt-1 text-xs text-slate-500"
                    >
                      <span
                        className="w-1.5 h-1.5 rounded-full"
                        style={{ backgroundColor: CATEGORY_COLORS[exp.category] }}
                      />
                      {categoryLabel(exp.category)}
                    </span>
                  </td>
                  <td className="px-5 py-3.5 hidden sm:table-cell">
                    <span className="inline-flex items-center gap-1.5 text-slate-600">
                      <span
                        className="w-1.5 h-1.5 rounded-full"
                        style={{ backgroundColor: CATEGORY_COLORS[exp.category] }}
                      />
                      {categoryLabel(exp.category)}
                    </span>
                  </td>
                  <td className="px-5 py-3.5 text-slate-500">{formatDate(exp.transactionDate)}</td>
                  <td className="px-5 py-3.5 text-right font-semibold text-red-500 whitespace-nowrap">
                    {formatCurrency(exp.amount)}
                  </td>
                  <td className="px-5 py-3.5 text-right whitespace-nowrap">
                    <button
                      onClick={() => setEditing(exp)}
                      className="text-slate-400 hover:text-brand-600 p-1.5 rounded-md hover:bg-brand-50 transition-colors"
                      aria-label="Edit"
                    >
                      <Pencil size={15} />
                    </button>
                    <button
                      onClick={() => setDeleting(exp)}
                      className="text-slate-400 hover:text-red-600 p-1.5 rounded-md hover:bg-red-50 transition-colors ml-1"
                      aria-label="Delete"
                    >
                      <Trash2 size={15} />
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showForm && <ExpenseForm onSubmit={handleCreate} onClose={() => setShowForm(false)} />}
      {editing && (
        <ExpenseForm initialData={editing} onSubmit={handleUpdate} onClose={() => setEditing(null)} />
      )}
      {deleting && (
        <ConfirmDialog
          title="Delete this expense?"
          description={`"${deleting.title}" will be permanently removed.`}
          onConfirm={handleDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </Layout>
  )
}
