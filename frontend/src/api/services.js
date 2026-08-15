import apiClient from './client'

export const authApi = {
  // Register does NOT return a token in this backend — user must log in separately.
  register: (data) => apiClient.post('/auth/register', data),
  login: (data) => apiClient.post('/auth/login', data)
}

export const userApi = {
  getProfile: () => apiClient.get('/users/profile')
}

export const expenseApi = {
  // Plain array response, with optional filter query params.
  list: (filters = {}) => apiClient.get('/expenses', { params: filters }),
  latest: () => apiClient.get('/expenses/latest'),
  create: (data) => apiClient.post('/expenses', data),
  update: (id, data) => apiClient.put(`/expenses/${id}`, data),
  remove: (id) => apiClient.delete(`/expenses/${id}`)
}

export const incomeApi = {
  list: () => apiClient.get('/incomes'),
  latest: () => apiClient.get('/incomes/latest'),
  create: (data) => apiClient.post('/incomes', data),
  update: (id, data) => apiClient.put(`/incomes/${id}`, data),
  remove: (id) => apiClient.delete(`/incomes/${id}`)
}

export const dashboardApi = {
  get: () => apiClient.get('/dashboard')
}
