import React, { useEffect, useState } from 'react'
import { UserRound, Mail, MapPin } from 'lucide-react'
import Layout from '../components/Layout'
import { userApi } from '../api/services'

export default function Profile() {
  const [profile, setProfile] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    userApi
      .getProfile()
      .then(({ data }) => setProfile(data))
      .catch(() => setError('Could not load profile.'))
  }, [])

  return (
    <Layout title="Profile">
      <div className="max-w-lg">
        {error && <p className="text-sm text-red-600 bg-red-50 rounded-lg px-3 py-2.5 mb-4">{error}</p>}

        {!profile ? (
          <div className="h-40 bg-slate-100 rounded-2xl animate-pulse" />
        ) : (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-card overflow-hidden">
            <div className="bg-gradient-to-br from-brand-500 to-brand-700 px-6 py-8 flex items-center gap-4">
              <div className="w-16 h-16 rounded-full bg-white/15 text-white flex items-center justify-center text-2xl font-semibold shrink-0">
                {profile.name?.charAt(0)?.toUpperCase()}
              </div>
              <div className="min-w-0">
                <p className="text-white font-semibold text-lg truncate">{profile.name}</p>
                <p className="text-brand-100 text-sm truncate">{profile.email}</p>
              </div>
            </div>

            <div className="p-6 space-y-1">
              <ProfileRow icon={UserRound} label="Full name" value={profile.name} />
              <ProfileRow icon={Mail} label="Email address" value={profile.email} />
              <ProfileRow icon={MapPin} label="Address" value={profile.address} last />
            </div>
          </div>
        )}
      </div>
    </Layout>
  )
}

function ProfileRow({ icon: Icon, label, value, last }) {
  return (
    <div className={`flex items-center gap-3 py-3.5 ${!last ? 'border-b border-slate-100' : ''}`}>
      <div className="w-9 h-9 rounded-lg bg-slate-100 text-slate-500 flex items-center justify-center shrink-0">
        <Icon size={16} />
      </div>
      <div className="min-w-0">
        <p className="text-xs text-slate-400">{label}</p>
        <p className="text-sm font-medium text-slate-800 truncate">{value}</p>
      </div>
    </div>
  )
}
