Satyam
2022462
https://github.com/Satyam22462/FlightTracker.git



---

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

| Layer | Technology |
|------|------------|
| UI | XML layouts, ConstraintLayout, RecyclerView |
| Logic | Kotlin, Coroutines, Lifecycle-aware components |
| API | AviationStack (REST), Retrofit + Gson |
| Storage | Room Database |
| Background Jobs | WorkManager (PeriodicWorker) |
| Navigation | Intent-based |

---

## 📂 Project Structure

```
app/
│
├── MainActivity.kt                     # Entry point, allows flight or route-based tracking
├── FlightStatusActivity.kt            # Shows live flight status via AviationStack
├── FlightWebActivity.kt               # Google WebView fallback for tracking
├── RouteStatusActivity.kt             # Lets users select flights for a route and shows avg. time
├── FlightWorker.kt                    # Background task fetching 3+ flights per route per day
├── FlightHistoryActivity.kt           # Displays flight history stored in Room DB
│
├── FlightRecord.kt                    # Room entity and DAO
├── FlightDatabase.kt                  # Room DB Singleton
├── AviationStackService.kt            # Retrofit API interface
├── FlightData.kt                      # Model for parsing API response
│
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── activity_flight_status.xml
│   │   ├── activity_route_status.xml
│   │   ├── activity_flight_history.xml
│   │   ├── item_flight_checkbox.xml
│   │   └── item_flight_record.xml
│   └── drawable/
│       ├── ic_flight.xml
│       ├── ic_refresh.xml
│       ├── ic_history.xml
│       └── gradient_bg.xml
```

---

## 🔁 Background Data Collection

The app collects **at least 3 valid flights per day** between a selected route using a scheduled background job via `WorkManager`.

- It stores them for 7 days in a local Room DB.
- Calculates the **average time taken**, factoring in delays.

---

## 📸 Screenshots

Coming soon…

---

## 🧪 How to Run

1. Clone the repo:
   ```bash
   git clone https://github.com/your-username/flight-tracker.git
   ```

2. Open in **Android Studio Arctic Fox** or later.

3. Add your **AviationStack API key** in `FlightWorker.kt`:
   ```kotlin
   val apiKey = "YOUR_API_KEY"
   ```

4. Run on an emulator or device with internet access.

---

---

## 🙏 Acknowledgements

- [AviationStack API](https://aviationstack.com/)
- Icons from Material Design
- Retrofit + Room Jetpack Libraries

---

