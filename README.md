# Expense Tracker Application

Expense Tracker is a full-stack personal finance application built with a **Spring Boot** backend (Java 17, Spring Security with JWT authentication, Spring Data JPA, PostgreSQL) and a **React** frontend (Vite, Tailwind CSS, Axios, Recharts). It enables users to securely log and track incomes and expenses, monitor real-time total and monthly balances, view category spending breakdowns, update profiles, and manage role-based dashboard access.

---

## 📋 Table of Contents

- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Prerequisites & System Requirements](#-prerequisites--system-requirements)
- [Environment Setup](#-environment-setup)
- [Installing Dependencies & Running Locally](#-installing-dependencies--running-locally)
  - [1. Backend Setup (Spring Boot)](#1-backend-setup-spring-boot)
  - [2. Frontend Setup (React / Vite)](#2-frontend-setup-react--vite)
- [Admin Credentials & Dashboard Access](#-admin-credentials--dashboard-access)
- [Running Backend Test Suites](#-running-backend-test-suites)
- [Project Directory Structure](#-project-directory-structure)

---

## ✨ Features

- 🔒 **Authentication & Authorization**: Secure JWT-based registration and login with BCrypt password hashing.
- 👤 **Role-Based Access Control (RBAC)**: Dedicated `ADMIN` role with access to system-wide dashboard metrics (`/dashboard`). Standard `USER` accounts access personal expense & income management.
- 📊 **Interactive Dashboard**: Real-time summary of total income, total expenses, net balance, category spending breakdown (donut chart), and recent transactions.
- 💸 **Expense & Income Tracking**: Full CRUD operations for managing income sources and categorised expense logs with filtering and debounced search.
- ⚙️ **User Profile Management**: View and update account profile information.

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.4 / 4.x (Java 17)
- **Security**: Spring Security with JWT (JSON Web Tokens)
- **Persistence**: Spring Data JPA / Hibernate
- **Database**: PostgreSQL (Production/Dev), H2 (In-memory for testing)
- **Build Tool**: Apache Maven (`mvnw` wrapper included)

### Frontend
- **Framework**: React 18 (Vite)
- **Styling**: Tailwind CSS
- **HTTP Client**: Axios (with JWT request interceptor)
- **Data Visualization**: Recharts
- **Icons**: Lucide React
- **Routing**: React Router DOM v6

---

## ⚙️ Prerequisites & System Requirements

Ensure the following tools are installed on your machine before running the application:

1. **Java Development Kit (JDK)**: Java 17 or higher
   ```bash
   java -version
   ```
2. **Apache Maven**: Version 3.8+ (or use the included Maven Wrapper `./mvnw` / `mvnw.cmd`)
   ```bash
   mvn -version
   ```
3. **Node.js & npm**: Node.js v18+ and `npm` v9+
   ```bash
   node -v
   npm -v
   ```
4. **PostgreSQL Database Server**: v13 or higher (running locally or accessible via network)
   ```bash
   psql --version
   ```

---

## 🔧 Environment Setup

### 1. Database Creation
Create a PostgreSQL database named `slt_expense_tracker` prior to launching the backend:

```sql
CREATE DATABASE slt_expense_tracker;
```

### 2. Backend Configuration
The database connection details and application configurations are defined in [`backend/src/main/resources/application.properties`](backend/src/main/resources/application.properties). Update the connection details if your local PostgreSQL username or password differs:

```properties
# PostgreSQL Database Connection
spring.datasource.url=jdbc:postgresql://localhost:5432/slt_expense_tracker
spring.datasource.username=postgres
spring.datasource.password=123

# JWT Configuration
jwt.secret=ThisIsASecureJwtSecretKeyForSLTExpenseTrackerAssessment2026
jwt.expiration=86400000

# Default Admin Credentials (Seeded on startup)
admin.email=${ADMIN_EMAIL:admin@example.com}
admin.password=${ADMIN_PASSWORD:Admin@12345}
```

> **Note**: You can also override configuration settings using environment variables (e.g., `ADMIN_EMAIL`, `ADMIN_PASSWORD`).

### 3. Frontend Configuration (Optional)
The React frontend is configured to call `http://localhost:8080/api` by default. If your backend server runs on a different port or host, create/update `.env` in the `frontend` folder based on `.env.example`:

```bash
cd frontend
cp .env.example .env
```

---

## 🚀 Installing Dependencies & Running Locally

### 1. Backend Setup (Spring Boot)

Navigate to the `backend` directory and compile/run the backend server:

#### **Windows (PowerShell / Command Prompt)**:
```powershell
cd backend
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

#### **Linux / macOS**:
```bash
cd backend
./mvnw clean compile
./mvnw spring-boot:run
```

> The Spring Boot backend server will start on port **`8080`** (`http://localhost:8080`).

---

### 2. Frontend Setup (React / Vite)

In a separate terminal window, navigate to the `frontend` directory, install node modules, and start the development server:

```bash
cd frontend
npm install
npm run dev
```

> The Vite frontend dev server will start on **`http://localhost:5173`**.

---

## 🔑 Admin Credentials & Dashboard Access

The backend automatically seeds an Administrator account into the database upon application startup via `AdminSeeder`.

### Default Admin Credentials

| Parameter | Default Value |
| :--- | :--- |
| **Email** | `admin@example.com` |
| **Password** | `Admin@12345` |
| **Role** | `ADMIN` |

### How to Access the Dashboard as Admin
1. Ensure both **Backend** (`http://localhost:8080`) and **Frontend** (`http://localhost:5173`) are running.
2. Open your browser and navigate to `http://localhost:5173/login`.
3. Enter `admin@example.com` and `Admin@12345`, then click **Sign In**.
4. Upon successful login, you will be redirected to the Admin **Dashboard** (`http://localhost:5173/dashboard`).
5. Standard users (`USER` role) registering through the application will be redirected to `/expenses`.

---

## 🧪 Running Backend Test Suites

The backend includes a comprehensive unit and integration test suite (39 tests) covering controllers, services, repositories, and security authentication.

> 💡 **Note**: Backend tests run against an **isolated H2 in-memory database** (configured in `backend/src/test/resources/application.properties` with PostgreSQL mode enabled). **You do NOT need PostgreSQL running to run the test suite.**

### Running All Backend Tests (39 Tests)

From the `backend/` directory:

#### **Windows (PowerShell / Command Prompt)**:
```powershell
cd backend
.\mvnw.cmd test
```

#### **Linux / macOS**:
```bash
cd backend
./mvnw test
```

### Running Specific Test Classes
To run individual test classes (e.g., `ExpenseServiceTest` or `AuthIntegrationTest`):

```powershell
# Windows
.\mvnw.cmd test -Dtest=ExpenseServiceTest
.\mvnw.cmd test -Dtest=AuthIntegrationTest

# Linux / macOS
./mvnw test -Dtest=ExpenseServiceTest
./mvnw test -Dtest=AuthIntegrationTest
```

### Clean & Re-run Test Suite
```powershell
.\mvnw.cmd clean test
```

---

## 📁 Project Directory Structure

```
SLT-Assessment/
├── backend/                        # Spring Boot Backend Project
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/slt/expense_tracker/
│   │   │   │   ├── config/          # Security & CORS configuration
│   │   │   │   ├── controller/      # REST API endpoints
│   │   │   │   ├── model/           # JPA Entities & Enums
│   │   │   │   ├── repository/      # Spring Data JPA repositories
│   │   │   │   ├── security/        # JWT utilities & filters
│   │   │   │   └── service/         # Business logic services
│   │   │   └── resources/           # Application configuration & seed settings
│   │   └── test/                    # Unit & Integration test suites (H2 in-memory)
│   ├── mvnw & mvnw.cmd              # Maven Wrapper scripts
│   └── pom.xml                      # Maven project configuration & dependencies
│
└── frontend/                       # React / Vite Frontend Project
    ├── src/
    │   ├── api/                     # Axios client & service calls
    │   ├── components/              # Reusable UI components & layouts
    │   ├── context/                 # Auth Context state provider
    │   ├── pages/                   # Application view pages (Dashboard, Expenses, etc.)
    │   └── utils/                   # Formatting utilities & constants
    ├── package.json                 # Frontend dependencies & scripts
    └── vite.config.js               # Vite build configuration
```
