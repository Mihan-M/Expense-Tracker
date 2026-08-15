import React, { useEffect, useState, useCallback } from 'react'
import { Plus, Pencil, Trash2, Wallet } from 'lucide-react'
import Layout from '../components/Layout'
import IncomeForm from '../components/IncomeForm'
import ConfirmDialog from '../components/ConfirmDialog'
import EmptyState from '../components/EmptyState'
import { incomeApi } from '../api/services'
import { formatCurrency, formatDate } from '../utils/format'

export default function Income() {
  const [incomes, setIncomes] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [showForm, setShowForm] = useState(false)
  const [editing, setEditing] = useState(null)
  const [deleting, setDeleting] = useState(null)

  const loadIncomes = useCallback(() => {
    setLoading(true)
    incomeApi
      .list()
      .then(({ data }) => setIncomes(data))
      .catch(() => setError('Could not load income records.'))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    loadIncomes()
  }, [loadIncomes])

  const handleCreate = async (payload) => {
    await incomeApi.create(payload)
    setShowForm(false)
    loadIncomes()
  }

  const handleUpdate = async (payload) => {
    await incomeApi.update(editing.id, payload)
    setEditing(null)
    loadIncomes()
  }

  const handleDelete = async () => {
    await incomeApi.remove(deleting.id)
    setDeleting(null)
    loadIncomes()
  }

  return (
    <Layout
      title="Income"
      actions={
        <button
          onClick={() => setShowForm(true)}
          className="hidden sm:flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-brand-500 text-white text-sm font-medium hover:bg-brand-600 transition-colors"
        >
          <Plus size={16} /> Add income
        </button>
      }
    >
      <button
        onClick={() => setShowForm(true)}
        className="sm:hidden mb-4 flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-brand-500 text-white text-sm font-medium hover:bg-brand-600 transition-colors"
      >
        <Plus size={16} /> Add income
      </button>

      {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2.5 mb-4">{error}</p>}

      <div className="bg-white rounded-2xl border border-slate-200 shadow-card overflow-hidden">
        {loading ? (
          <div className="p-6 space-y-3 animate-pulse">
            {[0, 1, 2, 3].map((i) => (
              <div key={i} className="h-12 bg-slate-100 rounded-lg" />
            ))}
          </div>
        ) : incomes.length === 0 ? (
          <EmptyState icon={Wallet} title="No income recorded yet" description="Add your first income entry to start tracking." />
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-slate-50 text-slate-500 text-xs uppercase tracking-wide">
              <tr>
                <th className="text-left px-5 py-3 font-medium">Title</th>
                <th className="text-left px-5 py-3 font-medium">Date</th>
                <th className="text-right px-5 py-3 font-medium">Amount</th>
                <th className="px-5 py-3"></th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {incomes.map((inc) => (
                <tr key={inc.id} className="hover:bg-slate-50/70 transition-colors">
                  <td className="px-5 py-3.5">
                    <p className="font-medium text-slate-800">{inc.title}</p>
                    {inc.note && <p className="text-xs text-slate-400 mt-0.5">{inc.note}</p>}
                  </td>
                  <td className="px-5 py-3.5 text-slate-500">{formatDate(inc.incomeDate)}</td>
                  <td className="px-5 py-3.5 text-right font-semibold text-emerald-600 whitespace-nowrap">
                    {formatCurrency(inc.amount)}
                  </td>
                  <td className="px-5 py-3.5 text-right whitespace-nowrap">
                    <button
                      onClick={() => setEditing(inc)}
                      className="text-slate-400 hover:text-brand-600 p-1.5 rounded-md hover:bg-brand-50 transition-colors"
                      aria-label="Edit"
                    >
                      <Pencil size={15} />
                    </button>
                    <button
                      onClick={() => setDeleting(inc)}
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

      {showForm && <IncomeForm onSubmit={handleCreate} onClose={() => setShowForm(false)} />}
      {editing && (
        <IncomeForm initialData={editing} onSubmit={handleUpdate} onClose={() => setEditing(null)} />
      )}
      {deleting && (
        <ConfirmDialog
          title="Delete this income record?"
          description={`"${deleting.title}" will be permanently removed.`}
          onConfirm={handleDelete}
          onCancel={() => setDeleting(null)}
        />
      )}
    </Layout>
  )
}
