# 💰 FakkaFlow — Personal Budgeting App

> CS251 · Introduction to Software Engineering · Cairo University · FCAI · 2026  
> Team: Beshoy Fomail · Mohamed Tamer · Mazen Magdy · Omar Hassan

---

## 📋 Table of Contents

- [Overview](#overview)
- [Features & User Stories](#features--user-stories)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Design Patterns & SOLID](#design-patterns--solid)
- [Database Schema](#database-schema)
- [Prerequisites](#prerequisites)
- [Installation & Running](#installation--running)
- [How to Use the App](#how-to-use-the-app)
- [Team Ownership](#team-ownership)

---

## Overview

FakkaFlow is a desktop personal budgeting application built with **Java 17** and **JavaFX**. It allows users to track income and expenses, set spending budgets per category, monitor financial goals, receive real-time budget alerts, and generate visual reports — all stored locally in a **SQLite** database with no internet required after setup.

---

## Features & User Stories

| # | User Story | What it does |
|---|-----------|-------------|
| US1 | **Sign Up** | Register with name, email, and password. Password is hashed with BCrypt before storing. Email format and password strength are validated. |
| US2 | **Login** | Authenticate with email and password. Invalid credentials show a clear error. On success, session is created and user lands on Dashboard. |
| US3 | **Add Transaction** | Log income or expense with amount, category, and an optional note. Amount is validated (must be > 0). Transaction is saved instantly and reflected on the dashboard. |
| US4 | **Create / Edit Budget** | Set up a budget cycle (total allowance, start date, end date). Also set per-category spending limits (e.g. Food: 1000 EGP/month). Duplicate category budgets are updated, not duplicated. |
| US5 | **Budget Over-Limit Alert** | After every transaction, the system checks all category budgets. If a category hits 80% it shows a yellow warning. If it hits 100%+ it shows a red exceeded alert on both the Budget and Dashboard screens. |
| US6 | **Set & Track Financial Goals** | Create a savings goal with a name, target amount, optional deadline, and initial saved amount. Add contributions over time. A progress bar and monthly savings needed are calculated automatically. |
| US7 | **Generate Financial Reports** | View spending broken down by category (pie chart), income vs expenses over time (bar chart), and auto-generated insights (top spending category, average per category). Filter by This Month / Last 3 Months / This Year / All Time. |

---

## Tech Stack

| Technology | Version | Role |
|-----------|---------|------|
| **Java** | 17 | Core application language |
| **JavaFX** | 21.0.2 | Desktop GUI framework (scenes, controls, charts) |
| **SQLite** | via JDBC 3.45.1 | Local relational database, no server needed |
| **jBCrypt** | 0.4 | Secure password hashing (Blowfish algorithm) |
| **Maven** | 3.6+ | Build tool, dependency management |

### Why these choices?

- **JavaFX** — Cross-platform desktop UI that integrates natively with Java. Supports CSS theming, charts, and rich controls out of the box.
- **SQLite** — Zero-configuration embedded database. The entire database is one `.db` file that gets created automatically on first run. Perfect for a desktop app with no server.
- **BCrypt** — Industry-standard password hashing with built-in salting. Protects user passwords even if the `.db` file is accessed directly.
- **Maven** — Handles all dependency downloads automatically. Every team member runs the same command and gets the same environment.

---

## Architecture

The app follows a strict **3-Layer Architecture** matching the SDS document:

```
┌─────────────────────────────────────────────────────────┐
│                    UI LAYER (JavaFX)                     │
│  LoginScreen · DashboardScreen · TransactionScreen       │
│  BudgetScreen · GoalScreen · ReportScreen · SideNav      │
└───────────────────────┬─────────────────────────────────┘
                        │ calls
┌───────────────────────▼─────────────────────────────────┐
│                   LOGIC LAYER                            │
│  AuthService · BudgetService · GoalService               │
│  AlertingService · ValidationService · SessionManager    │
│  RolloverEngine (Strategy Pattern)                       │
└───────────────────────┬─────────────────────────────────┘
                        │ calls
┌───────────────────────▼─────────────────────────────────┐
│                   DATA LAYER                             │
│  SQLiteDatabase · UserRepository · TransactionRepository │
│  BudgetRepository · CycleRepository · GoalRepository     │
│  Models: User · Transaction · Category · Budget · Goal   │
└─────────────────────────────────────────────────────────┘
                        │
                  fakkaflow.db (SQLite file)
```

Each layer only talks to the layer directly below it. The UI never touches SQL. The logic layer never builds JavaFX nodes. This makes each layer independently testable and replaceable.

---

## Project Structure

```
fakkaflow/
├── pom.xml                                   ← Maven config: deps, Java version, build plugins
├── README.md                                 ← This file
│
└── src/main/
    ├── resources/
    │   └── styles.css                        ← Full dark theme: colors, fonts, cards, tables, charts
    │
    └── java/com/fakkaflow/
        │
        ├── MainApp.java                      ← JavaFX Application subclass, DB init, first scene
        │
        ├── data/
        │   ├── model/
        │   │   ├── User.java                 ← id, name, email, passwordHash, createdAt
        │   │   ├── Transaction.java          ← id, userId, amount, type, categoryId, note, timestamp
        │   │   ├── Category.java             ← id, name (Food / Transport / Health / ...)
        │   │   ├── Budget.java               ← id, categoryId, amountLimit, period, spent (computed)
        │   │   ├── BudgetCycle.java          ← id, totalAllowance, startDate, endDate
        │   │   └── Goal.java                 ← id, name, targetAmount, savedAmount, deadline
        │   │
        │   └── repository/
        │       ├── SQLiteDatabase.java       ← Singleton DB connection, schema init, raw query/execute
        │       ├── UserRepository.java       ← findByEmail(), findById(), save()
        │       ├── TransactionRepository.java← save(), update(), delete(), findAll(), findByFilter()
        │       ├── CategoryRepository.java   ← findAll(), save() — seeded with 8 defaults
        │       ├── BudgetRepository.java     ← save() with upsert logic, findAll(), delete()
        │       ├── CycleRepository.java      ← save() replaces old cycle, load(), delete()
        │       └── GoalRepository.java       ← save(), updateSavedAmount(), findAll(), delete()
        │
        ├── logic/
        │   ├── service/
        │   │   ├── AuthService.java          ← registerUser() → validates → bcrypt → save → session
        │   │   │                               loginUser() → validate → bcrypt.check → session
        │   │   ├── ValidationService.java    ← validateEmail(), validatePasswordStrength(),
        │   │   │                               validateAmount(), validateAmountString(), validateName()
        │   │   ├── SessionManager.java       ← Singleton holding currentUser, createSession(), logout()
        │   │   ├── BudgetService.java        ← calculateSafeDailyLimit(), calculateRemainingBalance(),
        │   │   │                               startCycle(), saveBudget(), getBudgets(), handleRollover()
        │   │   ├── AlertingService.java      ← checkBudgets() → returns list of BudgetAlerts
        │   │   │                               with level NONE / WARNING (≥80%) / EXCEEDED (≥100%)
        │   │   └── GoalService.java          ← saveGoal(), addContribution(), deleteGoal(),
        │   │                                   calculateMonthlySavingsNeeded()
        │   │
        │   └── engine/
        │       └── RolloverEngine.java       ← calculateUnspent(), redistributeUnspent()
        │                                       Designed as Strategy Pattern (swappable algorithms)
        │
        └── ui/
            ├── util/
            │   ├── SceneManager.java         ← Singleton holding Stage, navigateTo() switches scenes
            │   └── UIFactory.java            ← Factory for styled controls: primaryBtn(), styledField(),
            │                                   card(), alertBadge(), heading(), errorLabel(), ...
            │
            └── view/
                ├── LoginScreen.java          ← US2: email + password fields, error label, link to sign up
                ├── SignUpScreen.java         ← US1: name/email/pass/confirm fields, feature highlights panel
                ├── SideNav.java             ← Sidebar with nav buttons, active state, logout button
                ├── DashboardScreen.java     ← Balance/income/expense/daily-limit cards,
                │                              cycle progress bar, budget alerts, recent 5 transactions
                ├── TransactionScreen.java   ← US3+US9: TableView of all transactions, type+category
                │                              filter bar, Add Transaction dialog with validation
                ├── BudgetScreen.java        ← US4+US5: cycle setup dialog, category budget cards
                │                              with progress bars and color-coded alerts
                ├── GoalScreen.java          ← US6: goal cards with progress bars, Add Goal dialog,
                │                              Contribute dialog, monthly savings calculation
                └── ReportScreen.java        ← US7: period selector, summary cards, PieChart by category,
                                               BarChart income vs expenses by month, insight badges
```

---

## Design Patterns & SOLID

### Strategy Pattern — `RolloverEngine`

The rollover behavior (what happens to unspent budget at end of cycle) is isolated in `RolloverEngine`. `BudgetService` is the context that delegates to it. You can swap strategies without touching `BudgetService`:

```
BudgetService (Context)
    └── uses → RolloverEngine (Concrete Strategy)

Future strategies you can add:
    ├── NoRolloverStrategy       → reset budget to zero each cycle
    ├── StandardRolloverStrategy → carry full remainder forward
    └── SmartRolloverStrategy    → redistribute by category spending history
```

### SOLID Principles

| Principle | How it's applied |
|-----------|-----------------|
| **S** — Single Responsibility | `TransactionRepository` only does SQL. `BudgetService` only does calculations. `AlertingService` only checks thresholds. No class does more than one job. |
| **O** — Open/Closed | `RolloverEngine` can be extended with new strategies without modifying `BudgetService`. |
| **I** — Interface Segregation | `ILoginView` and `IDashboardView` define only the methods each screen actually needs. Screens don't implement methods they don't use. |
| **D** — Dependency Inversion | `HistoryUIController` depends on `TransactionRepository` (abstraction), not on `SQLiteDatabase` directly. Swapping the DB engine only requires changing the repository layer. |

---

## Database Schema

The file `fakkaflow.db` is created automatically on first run.

```sql
users
  id INTEGER PRIMARY KEY AUTOINCREMENT
  name TEXT NOT NULL
  email TEXT UNIQUE NOT NULL
  password_hash TEXT NOT NULL          ← BCrypt hash, never plain text
  created_at TEXT DEFAULT datetime()

categories
  id INTEGER PRIMARY KEY AUTOINCREMENT
  name TEXT UNIQUE NOT NULL            ← Pre-seeded: Food, Transport, Shopping,
                                          Health, Entertainment, Utilities, Salary, Other

transactions
  id INTEGER PRIMARY KEY AUTOINCREMENT
  user_id INTEGER → users.id
  amount REAL NOT NULL
  type TEXT NOT NULL                   ← 'income' or 'expense'
  category_id INTEGER → categories.id
  note TEXT
  timestamp TEXT DEFAULT datetime()

budget_cycles
  id INTEGER PRIMARY KEY AUTOINCREMENT
  user_id INTEGER → users.id
  total_allowance REAL NOT NULL
  start_date TEXT NOT NULL
  end_date TEXT NOT NULL

budgets
  id INTEGER PRIMARY KEY AUTOINCREMENT
  user_id INTEGER → users.id
  category_id INTEGER → categories.id
  amount_limit REAL NOT NULL
  period TEXT NOT NULL                 ← 'Monthly', 'Weekly', 'Daily'

goals
  id INTEGER PRIMARY KEY AUTOINCREMENT
  user_id INTEGER → users.id
  name TEXT NOT NULL
  target_amount REAL NOT NULL
  saved_amount REAL DEFAULT 0
  deadline TEXT
  created_at TEXT DEFAULT datetime()
```

---

## Prerequisites

| Requirement | Minimum Version | Check with |
|-------------|----------------|------------|
| Java JDK | 17 | `java -version` |
| Maven | 3.6 | `mvn -version` |

### Install on your OS

**Arch Linux / EndeavourOS:**
```bash
sudo pacman -Syu
sudo pacman -S jdk17-openjdk maven
```

**Ubuntu / Debian:**
```bash
sudo apt update
sudo apt install openjdk-17-jdk maven
```

**macOS (Homebrew):**
```bash
brew install openjdk@17 maven
```

**Windows:**
1. Download JDK 17 from https://adoptium.net
2. Download Maven from https://maven.apache.org/download.cgi
3. Add both `bin/` folders to your `PATH` environment variable

---

## Installation & Running

```bash
# 1. Clone or extract the project
cd fakkaflow/

# 2. First run (downloads dependencies ~30MB, takes 1-2 min)
mvn javafx:run

# 3. Every run after that (instant)
mvn javafx:run
```

On first launch, `fakkaflow.db` is created in your working directory with all tables and default categories seeded.

### Running from an IDE

**IntelliJ IDEA:**
1. File → Open → select the `fakkaflow/` folder
2. IntelliJ detects it as a Maven project automatically
3. Wait for indexing to finish
4. Open `MainApp.java` → click the green ▶ Run button

**Eclipse:**
1. File → Import → Maven → Existing Maven Projects
2. Browse to the `fakkaflow/` folder → Finish
3. Right-click `MainApp.java` → Run As → Java Application

---

## How to Use the App

```
Launch app
    │
    ▼
Login Screen ──────────────────────────────────────── Sign Up Screen
    │  Enter email + password                              │  Name, email, password
    │  → lands on Dashboard                               │  → lands on Dashboard
    ▼
Dashboard
    ├── Balance card      (income − expenses)
    ├── Daily Limit card  (remaining ÷ days left in cycle)
    ├── Budget Alerts     (yellow = near limit, red = exceeded)
    └── Recent 5 transactions
    │
    ├── [Transactions] → View all · Filter by type/category · Add new transaction
    │
    ├── [Budgets] → Setup cycle (allowance + dates) · Add per-category limits
    │              Progress bars turn yellow at 80%, red at 100%
    │
    ├── [Goals] → Create savings goal · Add contributions · Track progress bar
    │             See monthly amount needed to reach goal by deadline
    │
    └── [Reports] → Select time period · View pie chart (spending by category)
                    Bar chart (monthly income vs expenses) · Read auto insights
```

---

## Team Ownership

| Member | Contributions |
|--------|--------------|
| **Mohamed Tamer** | Class diagram, Sequence diagrams 9, SOLID principles write-up, Strategy Pattern |
| **Beshoy Fomail** | Architecture diagram, Sequence diagrams 1 & 11 |
| **Mazen Magdy** | Sequence diagrams 5, 6, 7, 10 |
| **Omar Hassan** | Sequence diagrams 2, 3, 4, 8 |

---

*Built with ☕ Java · 📐 SDS-driven design · 🗄️ SQLite · 🎨 JavaFX*
