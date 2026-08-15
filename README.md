# Expense Tracker Application

Expense Tracker is a full-stack personal finance application built with Spring Boot (Java 17, Maven, Spring Security with JWT authentication) and React (Vite, Tailwind CSS, React Router). It enables users to securely log incomes and expenses, track real-time total and month-scoped balances, view category spending breakdowns, and manage their account profile.

---

## 🔑 Admin Login & Dashboard Access

Access to the **Dashboard** (`/dashboard` and `/api/dashboard`) is strictly restricted to users with the **`ADMIN`** role. Standard users (`USER` role) are automatically redirected to the Expenses page.

An administrator account is automatically seeded into the database on application startup via `AdminSeeder`.

### Default Admin Credentials

| Credential | Value |
| :--- | :--- |
| **Email** | `admin@example.com` |
| **Password** | `Admin@12345` |
| **Role** | `ADMIN` |

> **Note**: These credentials can be configured via environment variables or in `backend/src/main/resources/application.properties` using the `admin.email` and `admin.password` properties.

### How to Log In as Admin to Access the Dashboard

1. **Start the Applications**: Ensure both the backend server (`http://localhost:8080`) and frontend dev server (`http://localhost:5173`) are running.
2. **Navigate to Login**: Open your browser and go to `http://localhost:5173/login`.
3. **Enter Credentials**:
   - **Email**: `admin@example.com`
   - **Password**: `Admin@12345`
4. **Submit Login**: Click **Sign In**.
5. **Dashboard Redirection**: Upon successful authentication, the system recognizes the `ADMIN` role and automatically redirects you to `http://localhost:5173/dashboard`.
6. **Navigation Bar**: As an Admin, the **Dashboard** link will be visible in the left sidebar navigation.

---

## Prerequisites

- **Java Development Kit (JDK)**: Java 17 or higher
- **Build Tool**: Apache Maven 3.8+ (or use the included `./mvnw` wrapper)
- **Node.js**: Node v18+ and `npm`
- **Database**: PostgreSQL database server (running locally or accessible via network)

> **Note**: No Docker or `docker-compose` is used or required for this project.

---

## Backend Setup & Execution

### 1. Database Configuration
Configure your PostgreSQL connection details in `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/slt_expense_tracker
spring.datasource.username=postgres
spring.datasource.password=YOUR_POSTGRES_PASSWORD
```

Create the PostgreSQL database prior to launching:
```sql
CREATE DATABASE slt_expense_tracker;
```

### 2. Running the Backend Server
From the root directory or `backend/` directory:

```bash
# Using Maven wrapper (Windows PowerShell)
.\mvnw.cmd spring-boot:run

# Using Maven wrapper (Linux/macOS)
./mvnw spring-boot:run
```

The Spring Boot backend server starts on port **`8080`** (`http://localhost:8080`).

### 3. Running Backend Tests
To execute the complete unit and integration test suite (39 tests):

```bash
# Run unit & integration tests
.\mvnw.cmd test
```

---

## Frontend Setup & Execution

For detailed instructions on running, testing, and building the React frontend application, please refer to [`frontend/README.md`](frontend/README.md).

Quick start:
```bash
cd frontend
npm install
npm run dev
```
The Vite dev server runs at `http://localhost:5173`.
