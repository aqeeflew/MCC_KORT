# Kort App Audit & Integration Report

## 1. Executive Summary
This report provides a comprehensive, updated analysis of the **Kort App** project, based on the requirements outlined in `FILE_A.txt` (Group Project Task) and the documentation provided in `FILE_B.txt` (ICT602 Group Project Report). The audit involves a deep review of the actual Android source code to verify the implementation of cloud services and provides actionable recommendations for the required in-house web service.

## 2. Document Analysis
- **File A (Project Task)** mandates the development of a cloud-integrated mobile app requiring at least **three (3)** third-party cloud services and **one (1)** custom in-house web service (RESTful API).
- **File B (Documentation)** outlines the Kort App's design, targeting a sports court booking management system. It claims the integration of Google Firebase (Authentication & Realtime Database) and Google Maps API to fulfill the third-party requirements.

## 3. Audit: "Integration with Third-Party Cloud Services (Minimum 3)"
**Status: ACHIEVED**

A thorough review of the current source code confirms that the application successfully integrates three distinct third-party cloud services. This is a significant update from previous iterations, showing real progress.

### 1. Firebase Authentication
- **Implementation**: The app uses `FirebaseAuth` for user registration and login.
- **Evidence**: 
  - `LoginActivity.java`: Authenticates users via `mAuth.signInWithEmailAndPassword()`.
  - `RegisterActivity.java`: Registers new users via `mAuth.createUserWithEmailAndPassword()`.
- **Fulfillment**: Satisfies the Authentication (SaaS/PaaS) requirement.

### 2. Firebase Realtime Database
- **Implementation**: The app uses `FirebaseDatabase` to store and synchronize booking records in the cloud safely.
- **Evidence**: 
  - `BookingConfirmActivity.java`: Implements a transactional save to `mDatabase.child("bookingSlots")` to prevent double-booking, and writes booking details to `mDatabase.child("bookings")` and `mDatabase.child("userBookings")`.
- **Fulfillment**: Satisfies the Database (DBaaS) requirement.

### 3. Google Maps Platform
- **Implementation**: The app utilizes the Google Maps Android SDK to display the sports center location.
- **Evidence**:
  - `MainActivity.java`: Implements `OnMapReadyCallback` and uses a `SupportMapFragment` to render a map with a marker at coordinates (3.1390, 101.6869) for the "KORT Main Court".
  - `AndroidManifest.xml`: Contains the necessary `<meta-data android:name="com.google.android.geo.API_KEY" />`.
- **Fulfillment**: Satisfies the Location & Maps service requirement.

## 4. Audit: "Integration with In-House Web Service (Minimum 1)"
**Status: NOT ACHIEVED (Action Required)**

Currently, the Kort app does not communicate with any custom, in-house web service (RESTful API). All backend logic is either handled locally (via SQLite) or directly through Firebase. To fulfill this requirement, a custom backend API must be developed, deployed, and integrated into the Android app.

## 5. Recommendations for In-House Web Service

To meet the requirement, the in-house API must provide custom business logic not available from third-party services. Below are three detailed proposals you can choose from to implement for the Kort App:

### Proposal A: Smart Matchmaking API (Highly Recommended)
**Concept**: Allow users to find opponents or extra players to share a court with (e.g., needing 2 more players for a futsal match).
- **API Endpoint**: `POST /api/matchmaking/broadcast` (Broadcasts a need for players for a specific booking).
- **API Endpoint**: `GET /api/matchmaking/available` (Fetches available open matches for a user to join).
- **Business Logic**: The API checks the user's skill level and sport preference, matching them with suitable open games to avoid mismatched skill levels.

### Proposal B: Dynamic Pricing & Peak Hour API
**Concept**: Provide dynamic, real-time pricing for court bookings based on historical busyness and demand.
- **API Endpoint**: `GET /api/pricing?court={id}&date={date}&time={time}`.
- **Business Logic**: The API calculates the price of a court slot. If it's a known peak hour (e.g., Friday 8 PM), the price is standard. If it's an off-peak hour (e.g., Tuesday 10 AM), the API applies a custom discount algorithm to encourage bookings.

### Proposal C: Weather Integration & Court Advisory API
**Concept**: Specifically useful for outdoor courts (like Tennis or outdoor Futsal).
- **API Endpoint**: `GET /api/court-advisory?date={date}&time={time}`.
- **Business Logic**: The backend API acts as a middleman. It checks a third-party weather API (like OpenWeatherMap) and combines it with your court data. If rain is forecasted during a booked outdoor slot, your custom API returns a warning advisory, prompting the app to notify the user to reschedule or move to an indoor court.

### Implementation Guide for the chosen API
1. **Tech Stack**: Use **Node.js (Express)** or **Python (Flask/FastAPI)**.
2. **Hosting**: Deploy the API for free on platforms like **Render**, **Vercel**, or **Railway**.
3. **Android Integration**: Use a networking library like **Retrofit2** or **OkHttp** in your Android app to make HTTP requests to your deployed API endpoints and parse the JSON responses.
