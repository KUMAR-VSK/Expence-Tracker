# Expense Tracker - Android & Web Mini Player (v1.0.0)

A sleek, minimalist, offline-first Expense Tracker & Financial Management Application featuring an Android App (Jetpack Compose, Room DB, Hilt) and an interactive Web Mini Player (React, TypeScript, Vite).

Designed with a clean dark mode aesthetic, Indian Rupee (INR) formatting, and zero clutter.

---

## Features Overview

- Mobile Phone Mini Player:
  - Interactive device frame simulator with Dynamic Island notch, real-time status bar clock, and smooth navigation.
  - Floating Mini-Player Ticker Bar with real-time balance pulse & play/pause audio note playback simulator.

- INR Currency Native:
  - Strict Indian Rupee (INR) formatting across all transactions, category budgets, subscriptions, and financial goals.

- Simplified Payment Methods:
  - Native support for Google Pay (GPay) and Cash payments without asking for credit card or account details.

- 3-Bar Side Navigation Drawer:
  - Clean side drawer menu for navigating between Dashboard, History, Analytics, Budgets, Subscriptions, Savings Goals, and Category Management.

- Recurring Subscriptions Tracker:
  - Track monthly and yearly subscriptions (Netflix, Spotify, Cloud Storage, Gym Pass) with active/paused toggles.

- Savings Targets & Goals:
  - Set financial milestones (Emergency Fund, New Gadget, Vacation) with percentage progress bars and remaining targets.

- Category & Payment Management:
  - Add/remove custom expense & income categories with custom color tags.
  - Add/remove payment methods dynamically with local storage persistence.

- Version 1 Data Preservation Architecture:
  - Schema versioning tag (v1.0.0) ensures user data is preserved and never erased when upgrading to future app versions.

---

## Technology Stack

### Android Application
- Language: Kotlin 1.9.24
- UI Framework: Jetpack Compose (Material3)
- Database: Room Database 2.6.1 (SQLite)
- Dependency Injection: Hilt / Dagger
- Architecture: MVVM + Clean Architecture

### Web Application / Mini Player
- Framework: React 18 + Vite 8
- Language: TypeScript
- Styling: Modern Vanilla CSS, Glassmorphism, CSS Variables
- Icons: Lucide React

---

## Getting Started

### 1. Running the Web Mini Player (Mac / Desktop)

```bash
# Navigate to the web folder
cd web

# Install dependencies
npm install

# Start local dev server
npm run dev -- --port 5173
```
Open http://localhost:5173 in your browser.

---

### 2. Building the Android Debug APK

```bash
# Clean and assemble debug APK
./gradlew assembleDebug
```
The generated APK file is located at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## Mobile APK Testing via Local Web Server

1. Start the local server serving the built APK:
   ```bash
   python3 -m http.server 8080 --directory app/build/outputs/apk/debug
   ```
2. Connect your mobile device to the same Wi-Fi network and open:
   `http://<YOUR_MAC_IP>:8080/app-debug.apk`

---

## License
Licensed under the MIT License.