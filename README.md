# Expense Tracker - Technical Architecture & Specifications (v1.0.0)

A high-performance, offline-first mobile and web financial management platform built using Android Native (Kotlin, Jetpack Compose, Room ORM, Dagger Hilt) and a responsive Web Mini Player (React 18, TypeScript 5, Vite 8).

---

## 1. Technical Stack Specifications

### 1.1 Android Native Stack
- **Language**: Kotlin `1.9.24` (Target Compatibility: JVM 17)
- **Min SDK**: API Level `24` (Android 7.0)
- **Target SDK**: API Level `34` (Android 14.0)
- **Build System**: Gradle `8.14.3` with Kotlin DSL (`build.gradle.kts`)
- **UI Engine**: Jetpack Compose `1.5.8` with Material 3 Design Tokens
- **State Management**: Kotlin Coroutines `1.8.0` + `StateFlow` / `SharedFlow`
- **Database Engine**: Room ORM `2.6.1` over SQLite
- **Dependency Injection**: Dagger Hilt `2.50` (`@HiltViewModel`, `@AndroidEntryPoint`)
- **Navigation**: Jetpack Navigation Compose `2.7.7` with Type-Safe Route Arguments
- **Asynchronous Execution**: Dispatchers.IO for I/O operations & Dispatchers.Main for UI mutations

### 1.2 Web Mini Player Stack
- **Runtime / Framework**: React `18.3.1`
- **Language**: TypeScript `5.4` (Strict Type Checking Enabled)
- **Bundler / Tooling**: Vite `8.2.0`
- **Design System**: Vanilla CSS3, CSS Custom Properties, Glassmorphism Backdrop Filters
- **Iconography**: Lucide React (`lucide-react`)
- **Persistence Engine**: Web Storage API (Versioned LocalStorage Engine)

---

## 2. Feature Architecture

- **Mobile Phone Mini Player**:
  - Interactive device frame simulator with Dynamic Island notch, status bar clock, and smooth navigation.
  - Floating Mini-Player Ticker Bar with real-time balance pulse & voice note playback simulator.

- **Native INR (`₹`) Formatting**:
  - Strict Indian Rupee (INR) formatting across transactions, category budgets, subscriptions, and financial goals.

- **Simplified Payment Methods**:
  - Native support for **Google Pay** and **Cash** without requiring credit card or account details.

- **3-Bar Side Navigation Drawer Menu**:
  - Clean side drawer menu for navigating between Dashboard, History, Analytics, Budgets, Subscriptions, Savings Goals, and Category Management.

- **Back-Dated Transactions**:
  - Native Transaction Date Picker enabling entry of past/custom dates for historical record keeping.

- **Payment Mode Split & Category Percentage Charts**:
  - Interactive payment mode toggles (All, Google Pay, Cash) with visual percentage split meters and category breakdown badges.

- **Version 1 Data Preservation Architecture**:
  - Room ORM fallback configuration and schema tagging (`v1.0.0`) preserve database data across in-place APK updates and app upgrades.

---

## 3. System Architecture & Patterns

The platform strictly implements **Clean Architecture** combined with the **MVVM (Model-View-ViewModel)** architectural pattern.

```
+-------------------------------------------------------------------+
|                        Presentation Layer                         |
|   - Jetpack Compose Screens / React Views                         |
|   - ViewModels (StateFlow / React State)                          |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                           Domain Layer                            |
|   - Business Use Cases (ExpenseUseCases, CategoryUseCases)        |
|   - Domain Models (Expense, Category, Budget, PaymentMethod)      |
|   - Repository Interfaces                                         |
+-------------------------------------------------------------------+
                                  |
                                  v
+-------------------------------------------------------------------+
|                            Data Layer                             |
|   - DataRepository / ExpenseRepositoryImpl                        |
|   - Room DAOs (ExpenseDao, CategoryDao, PaymentMethodDao)         |
|   - SQLite Database & Versioned LocalStorage                      |
+-------------------------------------------------------------------+
```

---

## 4. Database Schema & Data Models

### 4.1 Entity Definitions

#### Expense Entity (`expenses`)
- `id` (Long, PrimaryKey, AutoGenerate)
- `title` (String, Non-Null)
- `amount` (Double, Non-Null)
- `type` (String: `"EXPENSE"` | `"INCOME"`)
- `categoryId` (Long, ForeignKey -> `categories.id`)
- `categoryName` (String)
- `categoryColor` (String)
- `paymentMethodId` (Long, ForeignKey -> `payment_methods.id`)
- `paymentMethodName` (String: `"Google Pay"` | `"Cash"`)
- `timestamp` (Long, Epoch Milliseconds / Selected Custom Date)
- `notes` (String, Optional)
- `isRecurring` (Boolean, Default: false)

#### Category Entity (`categories`)
- `id` (Long, PrimaryKey, AutoGenerate)
- `name` (String, Unique)
- `type` (String: `"EXPENSE"` | `"INCOME"`)
- `iconName` (String)
- `colorHex` (String)

#### Payment Method Entity (`payment_methods`)
- `id` (Long, PrimaryKey, AutoGenerate)
- `name` (String: `"Google Pay"` | `"Cash"`)
- `type` (String: `"UPI"` | `"CASH"`)
- `iconName` (String)

#### Budget Entity (`budgets`)
- `id` (Long, PrimaryKey, AutoGenerate)
- `categoryId` (Long)
- `limitAmount` (Double)
- `spentAmount` (Double)
- `monthYear` (String: `"YYYY-MM"`)

---

## 5. Data Persistence & Migration Policy

1. **Version 1 Schema Lock (`APP_VERSION = 1`)**:
   - Both Android Room ORM and Web LocalStorage tag state with schema version `1`.
2. **Zero Data Loss Migration Guarantee**:
   - Database updates use non-destructive migration policies (`fallbackToDestructiveMigrationOnDowngrade(dropAllTables = false)`).
   - In-place APK reinstallations preserve the internal SQLite database (`expense_tracker_db`).

---

## 6. Build & Test Commands

### 6.1 Android Build Tasks

```bash
# Execute Unit Test Suite
./gradlew test

# Compile Debug APK
./gradlew assembleDebug

# Compile Production Release APK
./gradlew assembleRelease
```

### 6.2 Web Mini Player Commands

```bash
# Navigate to web directory
cd web

# Install Node modules
npm install

# Run Vite Development Server
npm run dev -- --port 5173 --host

# Type-check and Bundle for Production
npm run build
```

---

## 7. License
Distributed under the MIT License.