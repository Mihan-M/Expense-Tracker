import React, { useState } from 'react'
import Modal from './Modal'
import { EXPENSE_CATEGORIES, categoryLabel } from '../utils/format'

const today = new Date().toISOString().slice(0, 10)

const emptyForm = {
  title: '',
  category: 'food',
  amount: '',
  transactionDate: today,
  note: ''
}

export default function ExpenseForm({ initialData, onSubmit, onClose }) {
  const [form, setForm] = useState(initialData || emptyForm)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleChange = (e) => {
    const { name, value } = e.target
    setForm((prev) => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await onSubmit({ ...form, amount: parseFloat(form.amount) })
    } catch (err) {
      if (err.response?.data?.errors) {
        const firstErr = Object.values(err.response.data.errors)[0]
        setError(firstErr || 'Failed to save expense')
      } else {
        setError(err.response?.data?.message || 'Failed to save expense')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Modal title={initialData ? 'Edit expense' : 'Add expense'} onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2">{error}</p>}

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1.5">Title</label>
          <input
            name="title"
            value={form.title}
            onChange={handleChange}
            required
            maxLength={150}
            placeholder="e.g. Grocery shopping"
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent"
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">Category</label>
            <select
              name="category"
              value={form.category}
              onChange={handleChange}
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent"
            >
              {EXPENSE_CATEGORIES.map((cat) => (
                <option key={cat} value={cat}>
                  {categoryLabel(cat)}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1.5">Amount (LKR)</label>
            <input
              type="number"
              step="0.01"
              min="0.01"
              name="amount"
              value={form.amount}
              onChange={handleChange}
              required
              className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent"
            />
          </div>
        </div>

        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1.5">Transaction date</label>
          <input
            type="date"
            name="transactionDate"
            value={form.transactionDate}
            onChange={handleChange}
            required
            min={today}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent"
          />

        </div>


        <div>
          <label className="block text-sm font-medium text-slate-700 mb-1.5">Note (optional)</label>
          <textarea
            name="note"
            value={form.note || ''}
            onChange={handleChange}
            rows={2}
            maxLength={500}
            className="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent"
          />
        </div>

        <div className="flex justify-end gap-3 pt-2">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium text-slate-600 hover:bg-slate-100 rounded-lg transition-colors"
          >
            Cancel
          </button>
          <button
            type="submit"
            disabled={submitting}
            className="px-4 py-2 text-sm font-medium text-white bg-brand-500 rounded-lg hover:bg-brand-600 disabled:opacity-60 transition-colors"
          >
            {submitting ? 'Saving…' : 'Save expense'}
          </button>
        </div>
      </form>
    </Modal>
  )
}
