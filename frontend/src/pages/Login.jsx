import React, { useState } from 'react'
import { Link, useNavigate, useLocation } from 'react-router-dom'
import { Landmark, TrendingUp, ShieldCheck, PieChart } from 'lucide-react'
import { useAuth } from '../context/AuthContext'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const successMessage = location.state?.message

  const handleChange = (e) => setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }))

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(form)
      navigate('/dashboard')
    } catch (err) {

      setError(err.response?.data?.message || 'Login failed. Please check your credentials.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:flex lg:w-1/2 bg-ink-950 relative overflow-hidden flex-col justify-between p-12">
        <div className="absolute -top-24 -right-24 w-96 h-96 rounded-full bg-brand-500/10 blur-3xl" />
        <div className="absolute bottom-0 left-0 w-72 h-72 rounded-full bg-brand-400/10 blur-3xl" />

        <div className="relative flex items-center gap-2.5">
          <div className="w-9 h-9 rounded-lg bg-brand-500 flex items-center justify-center">
            <Landmark size={18} className="text-white" />
          </div>
          <span className="font-semibold text-white text-lg tracking-tight">Expense Tracker</span>
        </div>

        <div className="relative">
          <h2 className="text-3xl font-semibold text-white leading-tight max-w-md">
            Know exactly where your money goes, every month.
          </h2>
          <p className="text-slate-400 mt-4 max-w-sm text-sm leading-relaxed">
            Track income and expenses, spot spending patterns by category, and keep a clear
            picture of your balance — all in one place.
          </p>

          <div className="mt-10 space-y-4">
            <FeatureRow icon={TrendingUp} text="Real-time dashboard with income vs. expense balance" />
            <FeatureRow icon={PieChart} text="Spending broken down by category, automatically" />
            <FeatureRow icon={ShieldCheck} text="Your data is scoped privately to your account" />
          </div>
        </div>

        <p className="relative text-xs text-slate-600">Built for the SLT Software Developer Assessment</p>
      </div>

      <div className="flex-1 flex items-center justify-center px-4 sm:px-8 py-12 bg-white">
        <div className="w-full max-w-sm">
          <div className="lg:hidden flex items-center gap-2.5 mb-8 justify-center">
            <div className="w-8 h-8 rounded-lg bg-brand-500 flex items-center justify-center">
              <Landmark size={16} className="text-white" />
            </div>
            <span className="font-semibold text-slate-900">Expense Tracker</span>
          </div>

          <h1 className="text-2xl font-semibold text-slate-900">Welcome back</h1>
          <p className="text-sm text-slate-500 mt-1">Log in to your account to continue</p>

          {successMessage && (
            <p className="text-sm text-emerald-700 bg-emerald-50 border border-emerald-100 rounded-lg px-3 py-2.5 mt-6">
              {successMessage}
            </p>
          )}
          {error && (
            <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2.5 mt-6">{error}</p>
          )}

          <form onSubmit={handleSubmit} className="space-y-4 mt-6">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Email</label>
              <input
                type="email"
                name="email"
                value={form.email}
                onChange={handleChange}
                required
                autoFocus
                className="w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1.5">Password</label>
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                required
                className="w-full rounded-lg border border-slate-300 px-3.5 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-brand-400 focus:border-transparent"
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              className="w-full py-2.5 rounded-lg bg-brand-500 text-white text-sm font-medium hover:bg-brand-600 disabled:opacity-60 transition-colors mt-2"
            >
              {loading ? 'Logging in…' : 'Log in'}
            </button>
          </form>

          <p className="text-sm text-slate-500 text-center mt-6">
            Don&apos;t have an account?{' '}
            <Link to="/register" className="text-brand-600 font-medium hover:underline">
              Create one
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}

function FeatureRow({ icon: Icon, text }) {
  return (
    <div className="flex items-center gap-3">
      <div className="w-8 h-8 rounded-lg bg-white/5 text-brand-400 flex items-center justify-center shrink-0">
        <Icon size={15} />
      </div>
      <span className="text-sm text-slate-300">{text}</span>
    </div>
  )
}
