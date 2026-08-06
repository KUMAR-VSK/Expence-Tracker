# Expense Tracker - Technical Architecture & Specifications (v1.0.0)

A high-performance, offline-first mobile and web financial management platform built on a **Unified Mobile-First Architecture**: a single responsive React web engine (React 19, TypeScript 6, Vite 8) served both on the web and inside an Android Native WebView runtime (Kotlin `2.1.0`, `WebChromeClient`).

---

## 1. Technical Stack Specifications

### 1.1 Android Native & WebView Stack
- **Language**: Kotlin `2.1.0` (Target Compatibility: JVM 17)
- **Min SDK**: API Level `24` (Android 7.0)
- **Target SDK**: API Level `36`
- **Build System**: Gradle `8.14.3` with Kotlin DSL (`build.gradle.kts`)
- **Runtime**: Android `WebView` loading bundled web assets via `WebViewAssetLoader` (`appassets.androidplatform.net`)
- **WebView Engine**: `WebChromeClient` with native file chooser launcher (`ActivityResultContracts.StartActivityForResult`)

### 1.2 Web Engine & Design System
- **Runtime / Framework**: React `19`
- **Language**: TypeScript `6.0` (Strict Type Checking Enabled)
- **Bundler / Tooling**: Vite `8.2.0` (Relative Base Path `base: './'`)
- **Charts**: Chart.js `4.x` via `react-chartjs-2`
- **Excel & File Parsing Engine**: SheetJS (`xlsx`)
- **Design System**: Mobile-First Responsive Breakpoint Grid, Glassmorphic CSS3, HSL Curated Colors
- **Iconography**: Lucide React (`lucide-react`)
- **Persistence Engine**: Web Storage API (Safe LocalStorage Purge Engine with in-memory fallback for WebView)

---

## 2. Key Platform Features

- **Unified Mobile-First Responsive Architecture**:
  - 100% UI, feature, and visual hierarchy parity between the Web App (`http://localhost:5173`) and the Android APK (`app-debug.apk`).
  - Mobile-first breakpoints system supporting `360dp`, `390dp`, `412dp`, `768px`, and `1024px+` viewports.

- **Add Bulk (Excel / CSV)**:
  - Spreadsheet ingestion for Excel (`.xlsx`, `.xls`) and CSV (`.csv`) files.
  - Interactive inline preview table for editing Title, Amount, Date, Category, and Payment Method before ingestion.
  - Native file chooser launcher integrated in Android `WebChromeClient`.
  - Downloadable pre-formatted sample `.csv` template.

- **Interactive Bottom Navigation & Home Indicator**:
  - Mobile bottom navigation dock featuring 1-tap navigation to **Home**, **History**, **Add Bulk**, and **Analytics**.
  - Clickable Home chassis indicator bar for instant home redirection.

- **Native INR (`₹`) Formatting**:
  - Strict Indian Rupee (INR) formatting across transactions, category budgets, subscriptions, and financial goals.

- **Simplified Payment Methods**:
  - Default support for **Google Pay** and **Cash** payments without asking for sensitive card details.

- **Complete In-App Data Reset**:
  - In-app glassmorphism modal to purge all transactions, clear local storage down to 0, and cleanly refresh state.

---

## 3. System Architecture & Responsive Breakpoints

```
+-------------------------------------------------------------------+
|                        Unified UI Layer                           |
|   - Mobile-First Responsive React Components                      |
|   - 360dp | 390dp | 412dp | 768px | 1024px Breakpoint System      |
+-------------------------------------------------------------------+
                                  |
            +---------------------+---------------------+
            |                                           |
            v                                           v
+-----------------------+                   +-----------------------+
|  Android Native APK   |                   |    Desktop/Mobile     |
|   (Android WebView)   |                   |     Web Engine        |
| - WebChromeClient     |                   | - Vite 8 Bundler      |
| - Local Assets Engine |                   | - LocalStorage API    |
+-----------------------+                   +-----------------------+
```

---

## 4. Build, Verification, & Deployment Tasks

### 4.1 Android Build Commands

```bash
# Build Clean Android Debug APK
./gradlew assembleDebug
```
The compiled APK artifact is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### 4.2 Web Engine & Asset Bundling Commands

```bash
# Navigate to web directory
cd web

# Install Dependencies
npm install

# Run Vite Development Server
npm run dev -- --port 5173 --host

# Lint (oxlint)
npm run lint

# Build Web Bundle with Relative Asset Paths
npm run build

# Bundle Web Assets into Android APK Assets
mkdir -p app/src/main/assets/web && cp -r web/dist/* app/src/main/assets/web/
```

---

## 5. License
Distributed under the MIT License.
