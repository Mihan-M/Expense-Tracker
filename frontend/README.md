# Expense Tracker — Frontend

React (Vite) single-page application for the SLT Software Developer / Intern take-home
assessment. Built to work against the existing Spring Boot backend in `../backend`
without any backend changes.

## Tech stack

- **React 18** via **Vite** (not Next.js, per the assessment requirement)
- **React Router** for client-side routing / SPA navigation
- **Tailwind CSS** for styling
- **Axios** for API calls, with a request interceptor that attaches the JWT
- **Recharts** for the category breakdown chart on the dashboard
- **lucide-react** for icons

This project requires a build step (Vite bundles and transpiles JSX). See below.

## Prerequisites

- Node.js 20+ and npm
- The backend running locally on `http://localhost:8080` (see `../backend/README.md`
  or run `mvn spring-boot:run` from the `backend` folder). The backend's
  `SecurityConfig` already allows CORS from `http://localhost:5173`, so no backend
  changes are needed to run the frontend in dev mode.

## Install & run (development)

```bash
cd frontend
npm install
cp .env.example .env    # only needed if your backend isn't on localhost:8080
npm run dev
```

Open `http://localhost:5173`. Register an account, then log in (see note below),
and you're in.

## Build for production

```bash
npm run build
```

This runs Vite's build step (JSX/TSX transpilation + bundling via esbuild/Rollup) and
outputs static files to `frontend/dist/`. Preview the production build locally with:

```bash
npm run preview
```

(`preview` serves on `http://localhost:4173`, which is also already whitelisted in the
backend's CORS config.)

## Important: registration does not log you in automatically

This backend's `/api/auth/register` endpoint returns an `AuthResponse` with **no JWT
token** — only `/api/auth/login` returns a token. The frontend reflects this exactly:
after registering, you're redirected to the login page with a "please log in" message,
rather than being silently signed in. This isn't a bug — it matches the backend's
actual contract, which I checked directly against your `AuthController`/`AuthResponse`/
`LoginResponse` source before writing this.

## How the frontend maps to your API

| Frontend page | Endpoint(s) used |
|---|---|
| Register | `POST /api/auth/register` |
| Login | `POST /api/auth/login` |
| Dashboard | `GET /api/dashboard` |
| Expenses | `GET/POST /api/expenses`, `PUT/DELETE /api/expenses/{id}` (list supports `category`, `startDate`, `endDate`, `search` query params — all wired up in the UI filter bar) |
| Income | `GET/POST /api/incomes`, `PUT/DELETE /api/incomes/{id}` |
| Profile | `GET /api/users/profile` |

A few field-name details worth knowing, since they differ from a "typical" scaffold:

- **Expense categories** are sent/received as lowercase strings (`"food"`,
  `"transport"`, etc.) — matching your `ExpenseCategory` enum's `@JsonValue`.
- **Income records** use `title` and `incomeDate` (not `source`/`receivedDate`) — there's
  no category field on income in your backend, so the UI doesn't show one.
- The **dashboard** shows `totalIncome`, `totalExpenses`, `balance`, a `categoryBreakdown`
  donut chart, and up to 10 `recentTransactions` — exactly what `DashboardResponse`
  returns. There's no month/year picker because the backend endpoint doesn't take one.

## Project structure

```
frontend/
├── src/
│   ├── api/
│   │   ├── client.js       Axios instance + JWT interceptor + 401 handling
│   │   └── services.js     Typed calls per resource (auth, users, expenses, incomes, dashboard)
│   ├── context/
│   │   └── AuthContext.jsx Session state (login persists JWT; register does not)
│   ├── components/         Sidebar, Topbar, Layout, forms, modal, charts, etc.
│   ├── pages/               Login, Register, Dashboard, Expenses, Income, Profile
│   └── utils/format.js      Currency/date formatting, category labels & colors
├── index.html
└── vite.config.js
```

## Notes on design choices

- **Sidebar layout** rather than a top navbar — reads more like a real financial
  dashboard product, and scales better once more sections get added.
- **Confirm dialogs** instead of `window.confirm()` for deletes — consistent styling,
  and doesn't block the JS thread with a native browser dialog.
- **Debounced search** (350ms) on the expense search filter so it doesn't fire a
  request on every keystroke.
- **Skeleton loading states** instead of a bare "Loading…" — closer to what a
  production dashboard would do.
