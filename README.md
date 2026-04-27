# FakkaFlow — Personal Budgeting App
CS251 · Cairo University · FCAI · 2026

## User Stories Implemented
- **US1** — User Sign-Up (name, email, password with bcrypt hashing, validation)
- **US2** — User Login (credential verification, session management)
- **US3** — Add Transaction (income/expense, category, note, amount validation)
- **US4** — Create/Edit Budget Cycle & Category Budgets (with conflict detection)
- **US5** — Budget Over-Limit Alerts (80% warning, 100%+ exceeded, live progress bars)
- **US6** — Set & Track Financial Goals (target, deadline, contributions, progress)
- **US7** — Generate Financial Reports (pie chart by category, bar chart income vs expenses, insights)

## Package Structure (matches SDS design)
```
src/main/java/com/fakkaflow/
├── MainApp.java                       ← JavaFX entry point
├── data/
│   ├── model/                         ← User, Transaction, Category, Budget, BudgetCycle, Goal
│   └── repository/                    ← SQLiteDatabase, UserRepo, TransactionRepo, ...
├── logic/
│   ├── service/                       ← AuthService, BudgetService, GoalService, AlertingService, ...
│   └── engine/                        ← RolloverEngine (Strategy Pattern)
└── ui/
    ├── util/                          ← SceneManager, UIFactory
    └── view/                          ← LoginScreen, SignUpScreen, DashboardScreen, ...
src/main/resources/
    └── styles.css                     ← Dark theme stylesheet
```

## SOLID Principles Applied
1. **SRP** — Each class has one job (BudgetService = calculations only, TransactionRepository = DB only)
2. **DIP** — Controllers depend on services/repos, not SQLiteDatabase directly
3. **ISP** — Each screen exposes only its own methods (HistoryScreen ≠ DashboardScreen)

## Design Pattern
- **Strategy Pattern** — `RolloverEngine` is a concrete strategy used by `BudgetService` (context). You can swap in `NoRolloverStrategy` or `SmartRolloverStrategy` without changing `BudgetService`.

## How to Run

### Option A — Maven (recommended, needs internet once)
```bash
# Install Java 17+ and Maven 3.6+
mvn javafx:run
```

### Option B — IntelliJ IDEA
1. Open project → Import as Maven project
2. Let Maven download dependencies
3. Run `MainApp.java`

### Option C — Eclipse
1. File → Import → Existing Maven Projects
2. Run → Run As → Java Application → MainApp

## Dependencies (auto-downloaded by Maven)
| Library | Version | Purpose |
|---------|---------|---------|
| OpenJFX | 21.0.2 | GUI framework |
| sqlite-jdbc | 3.45.1.0 | SQLite database driver |
| jBCrypt | 0.4 | Password hashing |

## Database
SQLite file `fakkaflow.db` is created automatically on first run in the working directory.
