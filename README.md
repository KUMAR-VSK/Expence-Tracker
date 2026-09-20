# Expense Tracker

A local-first personal finance and expense management application designed as a cross-platform hybrid system. The project features a React 19 single-page web application paired with an Android native wrapper built with Kotlin, Jetpack Compose, and AndroidX WebKit. All financial data remains strictly on the user's device.

---

## Architecture Overview

The system uses a hybrid architecture that separates the UI and business logic from the native mobile platform runtime, while delivering a native app experience on Android and standalone usability on the web.

```
+-----------------------------------------------------------------------+
|                             USER CLIENT                               |
+-----------------------------------+-----------------------------------+
|       Standalone Web Browser      |        Android Native Shell       |
|    (Chrome, Firefox, Safari)      |  (Kotlin + Jetpack Compose Shell) |
+-----------------------------------+-----------------+-----------------+
                                                      |
                                                      v
                                        +-------------------------------+
                                        |      WebViewAssetLoader       |
                                        | (https://appassets.android...)|
                                        +---------------+---------------+
                                                        |
+-------------------------------------------------------v---------------+
|                         REACT WEB APPLICATION                         |
|                                                                       |
|  [Presentation Layer]                                                 |
|    - Dashboard (Stat Cards, Animated Highlights, Recent Feeds)        |
|    - Transactions (Search, Multi-Filter, Expense Detail Modal)        |
|    - Analytics (Chart.js Doughnut, Bar, Trend Visualizations)         |
|    - Budgets (Category Limits, Progress Tracking, Overflow Alerts)    |
|    - Subscriptions (Recurring Cycles, Commitment Tracker)             |
|    - Categories & Payment Methods Management                          |
|    - Security (4-Digit PIN Keypad Lock Screen)                        |
|                                                                       |
|  [State & Business Logic]                                             |
|    - React State + Custom Persistence Hooks                           |
|    - SafeStorage Abstraction Layer                                    |
|    - CSV Ingestion and Validation Engine                              |
|    - Full State JSON Backup and Restore Module                        |
+-----------------------------------+-----------------------------------+
                                    |
                                    v
+-----------------------------------------------------------------------+
|                         DATA PERSISTENCE                              |
|          Web Storage API (localStorage) via SafeStorage Guard         |
+-----------------------------------------------------------------------+
```

### Architectural Layers

1. **Android Native Shell (`app/`)**
   - Built with **Kotlin 2.1** and **Jetpack Compose**.
   - Embeds an Android `WebView` configured with `WebViewAssetLoader` to securely load local assets via `https://appassets.androidplatform.net/web/index.html`.
   - Intercepts file chooser intents via `WebChromeClient.onShowFileChooser` and `ActivityResultContracts.StartActivityForResult`, allowing users to select CSV and JSON files directly from the Android system file picker.
   - Enforces edge-to-edge system display and routes web console logs directly to Android Logcat (`WebViewConsole`).

2. **Web Application Core (`web/`)**
   - Built with **React 19**, **TypeScript**, and **Vite**.
   - Modular component structure with clear separation between views (`DashboardView`, `TransactionsView`, `AnalyticsView`, `BudgetView`, `SubscriptionsView`, `CategoriesView`, `BulkImportView`) and utility services.
   - Rich visualizations powered by **Chart.js** and **react-chartjs-2** for expense breakdowns and income vs expense distributions.
   - Custom styling implemented in standard CSS with zero runtime utility framework overhead, providing responsive layouts, smooth micro-interactions, dark mode, and multiple device view frames (Phone Frame, Mini Player, and Full Screen).

3. **Data and Storage Layer**
   - Local-first architecture: All data is saved on the device via the `SafeStorage` wrapper (`web/src/utils/safeStorage.ts`) over the browser Web Storage API.
   - Safe serialization with fallback handling to prevent data corruption.
   - Complete export and import capabilities for both structured CSV files and full application snapshot JSON backups.

---

## Key Features

- **Financial Dashboard**: Real-time calculation of total balance, income, expenses, and a dedicated modal highlighting the highest recorded expense.
- **Transaction Management**: Create, edit, and delete transactions with category assignments, payment methods, dates, recurring tags, and notes.
- **Search and Filtering**: Instant search across titles and notes, filtered by transaction type (All, Expense, Income) or specific categories.
- **Visual Analytics**: Dynamic category breakdown charts and monthly cash flow comparisons using Chart.js.
- **Budget Tracking**: Set monthly spending limits per category, track consumed amounts in real time, and receive alerts when limits are exceeded.
- **Recurring Subscriptions**: Track recurring commitments, billing cycles (Monthly, Yearly), due dates, and active/paused status.
- **Bulk CSV Import**: Import hundreds of transactions at once with automated column mapping, row-by-row validation, duplicate protection, and error reporting.
- **Data Export and Backup**: Export transactions to CSV or save the entire database snapshot (transactions, categories, payment methods, budgets, subscriptions, settings) to a portable JSON file.
- **Security Lock**: Optional 4-digit numeric PIN protection with an interactive custom keypad lock screen.
- **Customization**: Support for multiple currencies (INR, USD, EUR, GBP), dark and light theme switching, and custom user profiles.
- **Multiple Layout Modes**: Toggle between Mobile Phone Frame, Compact Mini Player, and Expanded Full Screen views.

---

## Technology Stack

| Layer | Component | Version | Purpose |
| --- | --- | --- | --- |
| Mobile | Kotlin | 2.1.0 | Native Android programming language |
| Mobile | Android Gradle Plugin | 8.9.1 | Android build automation system |
| Mobile | Jetpack Compose BOM | 2026.03.01 | Declarative native Android UI toolkit |
| Mobile | Material 3 | AndroidX | Native Material Design 3 design system |
| Mobile | AndroidX WebKit | 1.11.0 | WebViewAssetLoader for secure local asset delivery |
| Web | React | 19.2.8 | Declarative frontend component library |
| Web | React DOM | 19.2.8 | DOM renderer for React |
| Web | TypeScript | 6.0.2 | Static typing and interfaces |
| Web | Vite | 8.2.0 | Frontend build tool and development server |
| Web | Chart.js | 4.5.1 | Data visualization and charts |
| Web | react-chartjs-2 | 5.3.1 | React wrapper for Chart.js |
| Web | Lucide React | 1.28.0 | Icon library |
| Web | Oxlint | 1.75.0 | High-performance JavaScript/TypeScript linter |

---

## Project Structure

```
.
|-- README.md                         # Project documentation
|-- build.gradle.kts                  # Root Gradle build configuration
|-- settings.gradle.kts               # Gradle project definitions
|-- gradle/                           # Gradle wrapper and version catalog
|   |-- libs.versions.toml            # Centralized dependency versions
|   `-- wrapper/                      # Gradle wrapper binaries
|-- app/                              # Android native shell
|   |-- build.gradle.kts              # Android module dependencies and SDK settings
|   |-- proguard-rules.pro            # Code obfuscation and optimization rules
|   `-- src/
|       `-- main/
|           |-- AndroidManifest.xml   # Android application manifest
|           |-- assets/
|           |   `-- web/              # Packaged web production bundle
|           |       |-- index.html
|           |       `-- assets/
|           |-- java/com/example/expensetracker/
|           |   |-- ExpenseTrackerApp.kt
|           |   |-- MainActivity.kt   # WebView and asset loader integration
|           |   `-- theme/            # Compose theme definitions
|           `-- res/                  # Android native resources, icons, themes
`-- web/                              # React frontend application
    |-- index.html                    # Single-page application entry point
    |-- package.json                  # Web dependencies and build scripts
    |-- tsconfig.json                 # TypeScript project configuration
    |-- vite.config.ts                # Vite build and plugin setup
    `-- src/
        |-- App.tsx                   # Main React application controller
        |-- index.css                 # Core CSS design system
        |-- types.ts                  # Domain models and TypeScript interfaces
        |-- components/               # Modular view components
        |   |-- AddExpenseModal.tsx
        |   |-- AnalyticsView.tsx
        |   |-- BudgetView.tsx
        |   |-- BulkImportModal.tsx
        |   |-- BulkImportView.tsx
        |   |-- CategoriesView.tsx
        |   |-- DashboardView.tsx
        |   |-- ExpenseDetailModal.tsx
        |   |-- LockScreen.tsx
        |   |-- PhoneFrame.tsx
        |   |-- SettingsModal.tsx
        |   |-- SubscriptionsView.tsx
        |   `-- TransactionsView.tsx
        |-- data/                     # Seed datasets and mock fixtures
        `-- utils/                    # Utility functions (CSV parser, SafeStorage)
```

---

## How to Run the Project

You can run the project in two ways:
1. **As a standalone web application** for fast local development and browser access.
2. **As an Android native application** on an emulator or physical Android device.

---

### Option 1: Running the Web Application

#### Prerequisites
- **Node.js**: Version 20.19+ or 22.12+ recommended
- **npm**: Version 10+

#### Steps

1. Navigate to the `web/` directory:
   ```bash
   cd web
   ```

2. Install project dependencies:
   ```bash
   npm install
   ```

3. Start the local development server:
   ```bash
   npm run dev
   ```

4. Open your browser and navigate to the address displayed in the terminal (typically `http://localhost:5173`).

5. Other available web commands:
   ```bash
   # Run type check and create production bundle in web/dist/
   npm run build

   # Run Oxlint for code quality validation
   npm run lint

   # Preview the production build locally
   npm run preview
   ```

---

### Option 2: Syncing Web Bundle to Android Assets

Whenever changes are made to the web source code, build the web distribution and sync it to the Android assets directory:

```bash
# 1. Build the web distribution
cd web
npm run build
cd ..

# 2. Sync distribution files into Android assets
rm -rf app/src/main/assets/web/*
cp -r web/dist/* app/src/main/assets/web/
```

---

### Option 3: Running the Android Application

#### Prerequisites
- **Java Development Kit (JDK)**: Version 17
- **Android SDK**: Build-Tools and Platform SDK for API 36 (Minimum SDK: 24)
- **Android Studio** (Ladybug / Meerkat or later) or Android SDK Command-Line Tools
- An active Android Virtual Device (AVD) or a USB-connected Android phone with USB debugging enabled

#### Using the Command Line

1. Make sure you are in the project root directory:
   ```bash
   cd /path/to/Expence-Tracker
   ```

2. Build the debug APK:
   ```bash
   ./gradlew assembleDebug
   ```

3. Install the APK to your connected device or running emulator:
   ```bash
   ./gradlew installDebug
   ```

4. Alternatively, launch the app directly:
   ```bash
   adb shell am start -n com.example.expensetracker/.MainActivity
   ```

The compiled APK file will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

#### Using Android Studio

1. Open Android Studio.
2. Select **File > Open...** and choose the root directory of this repository (`Expence-Tracker`).
3. Allow Gradle to synchronize dependencies.
4. Select `app` in the run configuration dropdown.
5. Choose your target emulator or connected physical device.
6. Click **Run** (green play button) or press `Shift + F10`.

---

## CSV Import Format Specification

The application supports importing records from standard UTF-8 encoded CSV files.

### Column Requirements

| Column Header | Required | Allowed Values / Format | Description |
| --- | --- | --- | --- |
| Date | Yes | `YYYY-MM-DD` or ISO 8601 | Transaction occurrence date |
| Title | Yes | String (1-100 characters) | Description of the transaction |
| Amount | Yes | Positive decimal / integer | Monetary value |
| Type | Yes | `EXPENSE` or `INCOME` | Transaction classification |
| Category | No | String | Category name (auto-created if not found) |
| PaymentMethod | No | `Google Pay`, `Cash`, `Card`, `UPI`, `Bank` | Method used for payment |
| Notes | No | String | Optional description or memo |

### Sample CSV

```csv
Date,Title,Amount,Type,Category,PaymentMethod,Notes
2026-08-01,Supermarket Groceries,3500,EXPENSE,Food & Dining,Google Pay,Weekly groceries
2026-08-02,Monthly Salary,85000,INCOME,Salary,Google Pay,August paycheck
2026-08-05,Electricity Bill,2200,EXPENSE,Bills & Utilities,Google Pay,Monthly utilities
2026-08-10,Freelance Project,15000,INCOME,Freelance,Google Pay,Website design payment
```

Constraints:
- File size limit: 1 MB.
- Maximum row count: 1,000 rows per batch.
- Rows missing `Date`, `Title`, or `Amount` are flagged with specific line numbers and excluded to preserve ledger accuracy.

---

## Backup and Restore (JSON)

The application enables exporting and restoring a complete snapshot of all stored records. The generated JSON structure includes:

```json
{
  "version": 1,
  "exportedAt": "2026-08-10T12:00:00.000Z",
  "expenses": [],
  "categories": [],
  "paymentMethods": [],
  "budgets": [],
  "subscriptions": [],
  "settings": {
    "currency": "₹",
    "darkMode": false,
    "isPinLocked": false,
    "pin": "",
    "viewMode": "PHONE_FRAME",
    "userName": "Kumar V S"
  }
}
```

Restoring from a valid backup replaces the current local store safely without requiring network connectivity.

---

## Security and Privacy

- **100% Local Storage**: All financial records, account details, and settings are stored locally on the client device.
- **No Cloud Tracking**: No external telemetry, advertising SDKs, or analytics backends are attached.
- **Application PIN**: An optional 4-digit PIN lock can be enabled in Settings to restrict access upon app launch.
- **File Chooser Sandboxing**: Native file interactions access only user-selected documents through standard Android storage provider contracts.

---

## Contribution Guidelines

1. Ensure code conforms to TypeScript and Android Kotlin style guidelines.
2. Run `npm run lint` inside the `web/` directory before proposing changes.
3. Validate that both web and Android debug builds compile without errors (`npm run build` and `./gradlew assembleDebug`).
4. Avoid adding external telemetry or tracking dependencies.

---

## License

This project is licensed under the MIT License. See the LICENSE file for details.