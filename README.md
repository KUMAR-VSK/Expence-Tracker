# Expense Tracker

A local-first personal expense tracker with one React application for web and Android. The Android app serves the same compiled web bundle from `WebViewAssetLoader`, so its interface and data behavior match the browser build.

## What it does

- Add, edit, delete, search, and filter income and expense transactions.
- Track category spending against monthly budgets.
- Manage categories, Google Pay/Cash payment methods, and recurring subscriptions.
- Preview, edit, and import CSV transactions; files are limited to 1 MB and 1,000 rows.
- Export and restore complete JSON backups. Restore data is validated before it replaces local state.
- Persist data locally in Web Storage, with an in-memory fallback if storage is unavailable.
- Use a four-digit screen lock, currency selection, and full local-data reset.

The app has no account system, server API, cloud sync, or encrypted data store. The PIN is a convenience screen lock, not a substitute for device security.

## Architecture

```text
React + TypeScript + Vite
        |
        +-- Browser: local Web Storage
        |
        +-- Android: Compose host -> WebViewAssetLoader -> bundled web assets
```

| Area | Technology |
| --- | --- |
| Web app | React 19, TypeScript 6, Vite 8 |
| Charts | Chart.js with react-chartjs-2 |
| Android host | Kotlin, Jetpack Compose, Android WebView |
| Minimum Android version | API 24 (Android 7.0) |
| Data storage | Browser/WebView localStorage |
| Import format | CSV only, parsed client-side without third-party spreadsheet code |

The Android WebView enables debugging only in debug builds and serves app files from `https://appassets.androidplatform.net`. File-URL access is disabled; CSV files are provided through Android's document picker.

## CSV import format

Download the in-app sample template or provide a UTF-8 CSV with these headers:

```csv
Date,Title,Amount,Type,Category,PaymentMethod,Notes
2026-08-01,Supermarket Groceries,3500,EXPENSE,Food & Dining,Google Pay,Weekly groceries
2026-08-02,Monthly Salary,85000,INCOME,Salary,Google Pay,August paycheck
```

`Date`, `Title`, and `Amount` are validated. `Type` must be `EXPENSE` or `INCOME`; when category or payment method is absent, the app supplies a suitable local default. Unsupported, oversized, malformed, or invalid rows are rejected instead of creating transactions.

## Prerequisites

- Node.js and npm
- JDK 17
- Android SDK API 36

## Develop and verify

Run all commands from the repository root.

```bash
# Install the web dependencies
npm --prefix web ci

# Run the web app locally
npm --prefix web run dev -- --host 0.0.0.0 --port 5173

# Run web checks and create a production bundle
npm --prefix web run lint
npm --prefix web run build

# Check production web dependencies for known vulnerabilities
npm --prefix web audit --omit=dev
```

To package the current web bundle in the Android application, rebuild the web app and synchronize its output before assembling the APK:

```bash
npm --prefix web run build
rsync -a --delete web/dist/ app/src/main/assets/web/
./gradlew testDebugUnitTest assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

## Project layout

```text
app/                    Android Compose/WebView host
app/src/main/assets/web Compiled web bundle packaged in the APK
web/src/                React application source
web/src/utils/          Local persistence and bounded CSV parsing
```

## Notes for contributors

- Keep `app/src/main/assets/web/` synchronized with `web/dist/` whenever changing web code; Android ships the bundled assets, not `web/src/`.
- Do not reintroduce a third-party spreadsheet parser without a current security review. The previous spreadsheet dependency had high-severity advisories.
- Keep at least one expense category, income category, and payment method. The UI and state handlers enforce this invariant.
- Validate the web build and Android debug build before submitting changes.

## License

Distributed under the MIT License.
