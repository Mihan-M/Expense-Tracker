/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#eef5ff',
          100: '#d9e8ff',
          200: '#b3d1ff',
          400: '#4f8dff',
          500: '#2f6fed',
          600: '#1f56c9',
          700: '#1a459c',
          900: '#0f2861'
        },
        ink: {
          950: '#0b1220',
          900: '#101828',
          800: '#1d2939'
        }
      },
      boxShadow: {
        card: '0 1px 2px 0 rgba(16, 24, 40, 0.04), 0 1px 3px 0 rgba(16, 24, 40, 0.06)'
      }
    }
  },
  plugins: []
}
