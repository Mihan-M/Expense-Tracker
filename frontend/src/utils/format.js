export function formatCurrency(amount) {
  const value = Number(amount ?? 0)
  return new Intl.NumberFormat('en-LK', {
    style: 'currency',
    currency: 'LKR',
    minimumFractionDigits: 2
  }).format(value)
}

export function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' })
}

// Matches ExpenseCategory enum values exactly (lowercase, via @JsonValue on the backend).
export const EXPENSE_CATEGORIES = ['food', 'transport', 'bills', 'shopping', 'entertainment', 'other']

export const CATEGORY_COLORS = {
  food: '#f97316',
  transport: '#3b82f6',
  bills: '#ef4444',
  shopping: '#a855f7',
  entertainment: '#ec4899',
  other: '#64748b'
}

export function categoryLabel(category) {
  if (!category) return ''
  return category.charAt(0).toUpperCase() + category.slice(1)
}
