# Expense Tracker Application

Expense Tracker is a full-stack personal finance application built with Spring Boot (Java 17, Maven, Spring Security with JWT authentication) and React (Vite, Tailwind CSS, React Router). It enables users to securely log incomes and expenses, track real-time total and month-scoped balances, view category spending breakdowns, and manage their account profile.

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
To execute the complete unit and integration test suite:

```bash
# Run unit & integration tests
.\mvnw.cmd test
```

### 4. Admin Account Seeding
An administrative user account is seeded automatically on initial application startup via `AdminSeeder`.
The admin credentials can be configured via environment variables or in `application.properties`:

- **Property Keys**: `admin.email` and `admin.password`
- **Default Seeded Admin Email**: `admin@example.com`
- **Default Seeded Admin Password**: `Admin@12345`

*Because access to `/api/dashboard` and the frontend `/dashboard` view requires the `ADMIN` role, log in with `admin@example.com` / `Admin@12345` to access the Dashboard.*

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
