# Expense Tracker - Android App

A local-first personal expense tracker built as a native Android application using Kotlin, Jetpack Compose, Room database, and Hilt dependency injection.

## What it does

- Add, edit, delete, search, and filter income and expense transactions
- Track category spending against monthly budgets
- Manage categories, Google Pay/Cash payment methods, and recurring subscriptions
- Import transactions from CSV files (1 MB limit, 1,000 rows max)
- Export and restore complete JSON backups
- Persist data locally in Room database (SQLite)
- Use a four-digit screen lock, currency selection, and full local-data reset

## Architecture

| Area | Technology |
| --- | --- |
| Language | Kotlin 2.1 |
| UI | Jetpack Compose (Material 3) |
| Database | Room 2.8.4 (SQLite) |
| Dependency Injection | Hilt 2.55 |
| Navigation | Navigation Compose |
| Charts | Compose Multiplatform Chart (or MPAndroidChart) |
| Minimum Android version | API 24 (Android 7.0) |
| Target Android version | API 36 |

## CSV Import Format

Download the in-app sample template or provide a UTF-8 CSV with these headers:

```csv
Date,Title,Amount,Type,Category,PaymentMethod,Notes
2026-08-01,Supermarket Groceries,3500,EXPENSE,Food & Dining,Google Pay,Weekly groceries
2026-08-02,Monthly Salary,85000,INCOME,Salary,Google Pay,August paycheck
```

`Date`, `Title`, and `Amount` are validated. `Type` must be `EXPENSE` or `INCOME`; when category or payment method is absent, the app supplies a suitable local default. Unsupported, oversized, malformed, or invalid rows are rejected instead of creating transactions.

## Prerequisites

- JDK 17
- Android SDK API 36
- Android Studio or command-line tools

## Build and Run

```bash
# Build debug APK
./gradlew assembleDebug

# Run unit tests
./gradlew testDebugUnitTest

# Run instrumented tests (requires device/emulator)
./gradlew connectedDebugAndroidTest
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Project Layout

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/expensetracker/
│   │   │   ├── data/           # Room database, DAOs, entities, repositories
│   │   │   ├── domain/         # Domain models, repository interfaces, use cases
│   │   │   ├── di/             # Hilt modules
│   │   │   ├── presentation/   # Compose screens, viewmodels, components
│   │   │   ├── theme/          # Material 3 theme
│   │   │   ├── utils/          # Currency formatting, date utils, export
│   │   │   ├── ExpenseTrackerApp.kt
│   │   │   └── MainActivity.kt
│   │   └── res/                # Resources, drawables, themes
│   └── androidTest/            # Instrumented tests
├── build.gradle.kts
└── proguard-rules.pro
```

## Notes for Contributors

- Follow the clean architecture layers: data → domain → presentation
- Room entities in `data/model/`, DAOs in `data/dao/`, repositories in `data/repository/`
- ViewModels in `presentation/viewmodel/`, screens in `presentation/screens/`
- Use Hilt for DI (`@HiltViewModel`, `@AndroidEntryPoint`)
- Validate the Android debug build before submitting changes

## License

Distributed under the MIT License.