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

### 1.2 Web Mini Player Stack
- **Runtime / Framework**: React `18.3.1`
- **Language**: TypeScript `5.4` (Strict Type Checking Enabled)
- **Bundler / Tooling**: Vite `8.2.0`
- **Excel Parsing Engine**: SheetJS (`xlsx`)
- **Design System**: Vanilla CSS3, CSS Custom Properties, Glassmorphism Backdrop Filters
- **Iconography**: Lucide React (`lucide-react`)
- **Persistence Engine**: Web Storage API (Versioned LocalStorage Engine)

---

## 2. Key Platform Features

- **Add Bulk (Excel / Photo OCR / PDF Statements)**:
  - Dedicated menu item for uploading spreadsheets (`.xlsx`, `.csv`), receipt photos (`.png`, `.jpg`), or PDF invoices (`.pdf`).
  - Interactive preview table allowing inline editing of Title, Amount, Date, and Payment Method before saving.
  - Downloadable pre-formatted sample `.csv` template.

- **Mobile Phone Mini Player**:
  - Interactive device frame simulator with Dynamic Island notch, real-time status bar clock, and smooth navigation drawer.

- **Native INR (`₹`) Formatting**:
  - Strict Indian Rupee (INR) formatting across transactions, category budgets, subscriptions, and financial goals.

- **Simplified Payment Methods**:
  - Support for **Google Pay** and **Cash** payments without asking for credit card / account numbers.

- **Back-Dated Transactions**:
  - Native Transaction Date Picker enabling entry of past or custom dates.

- **Monthly Wise Split & Payment Mode Charts**:
  - Interactive payment mode toggles (All, Google Pay, Cash) with visual percentage split meters and monthly category breakdown badges.

- **Complete In-App Data Reset**:
  - Clean in-app glassmorphism modal to purge all transactions and reset budget metrics to 0.

- **Version 1 Data Preservation Architecture**:
  - Non-destructive Room ORM database migration policy (`fallbackToDestructiveMigrationOnDowngrade(dropAllTables = false)`).

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
- `timestamp` (Long, Epoch Milliseconds / Custom Date)
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

## 5. Build, Verification, & Deployment Tasks

### 5.1 Android Build & Test Commands

```bash
# Execute Unit Test Suite
./gradlew test

# Compile Debug APK
./gradlew assembleDebug

# Compile Production Release APK
./gradlew assembleRelease
```
The generated APK artifact is located at:
`app/build/outputs/apk/debug/app-debug.apk`

### 5.2 Web Mini Player Commands

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

## 6. License
Distributed under the MIT License.