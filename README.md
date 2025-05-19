# ✈️ Flight Tracker 2.0 — Android App

An Android application that allows users to **track flights live** and **analyze average flight durations** (including delays) for specific routes. The app integrates with the [AviationStack API](https://aviationstack.com/) and uses **Room Database**, **Retrofit**, and **WorkManager** for background data collection and local storage.

---

## 📱 Features

### ✅ Real-Time Flight Tracking
- Enter a **flight number** (e.g., `EK215`) to track its live status.
- View:
  - Departure and arrival airports
  - Scheduled and live timings
  - Delay in minutes
  - Live coordinates (if available)

### ✅ Route-Based Flight Analytics
- Enter a **source and destination airport** (IATA codes, e.g., `JFK` to `LAX`)
- View at least 3 flights collected daily over 7 days
- Calculate and display **average flight time including delays**
- Select and save flights to analyze historical trends

### ✅ Flight History
- View all saved flight records in a dedicated screen.
- Review flight details including delays and durations.

---

## 🔧 Architecture & Tech Stack

| Layer       | Technology                          |
|-------------|--------------------------------------|
| UI          | XML layouts, ConstraintLayout, RecyclerView |
| Logic       | Kotlin, Coroutines, Lifecycle-aware components |
| API         | AviationStack (REST API), Retrofit + Gson |
| Storage     | Room Database                        |
| Background  | WorkManager (PeriodicWorker)         |
| Navigation  | Intent-based                         |

---



