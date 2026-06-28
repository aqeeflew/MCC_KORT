# Kort App - Pre-Flight Checklist

This checklist ensures that the project is cohesive, cross-platform compatible, and "executable-ready" for your team members when they pull from GitHub.

## 1. Cohesion (Styling & Logic)
- [ ] **UI/UX Consistency**: Verify all screens (Login, Main, Booking, Matchmaking, History) use the dark theme (`#000000` background, `#1A1A1A` cards, `#00C853` accents, and `#FFFFFF` text).
- [ ] **Database Integrity**: Ensure that bookings made on one account are not visible to another (tested the new `userId` SQLite logic).
- [ ] **Graceful Degradation**: If the Node.js matchmaking API is offline or not yet hosted, ensure the Android app safely falls back to the mock data so the app doesn't crash during demonstrations.
- [ ] **No Hardcoded Dead Ends**: Verify that users can actually select different courts (Court 1, Court 2) and pick their own time slots (08:00 AM - 10:00 PM).

## 2. Cross-Platform Compatibility (Windows vs. Pop!_OS 24.04)
- [ ] **Android App**: The project uses Gradle Wrapper (`gradlew` for Pop!_OS Linux and `gradlew.bat` for Windows). Because Gradle manages the build environment, it is 100% compatible across both OSes as long as Android Studio is installed.
- [ ] **Node.js API**: The matchmaking backend uses standard Node modules (`express`, `cors`, `firebase-admin`). These are purely JavaScript and will run identically on both Windows and Pop!_OS. 
- [ ] **File Paths**: Ensure there are no hardcoded OS-specific paths (e.g., `C:\Users\...` or `/home/user/...`) anywhere in your Java or Node.js code.

## 3. GitHub "Executable-Ready" Status (For Team Members)
If your team members pull this from GitHub, they can open it in Android Studio and hit "Run". However, ensure the following to avoid build errors:
- [ ] **`google-services.json`**: Firebase requires this file in the `app/` directory to run. If you added `google-services.json` to `.gitignore` for security, **your team members will need you to send them this file manually**, otherwise Android Studio will fail to compile the app. 
- [ ] **Matchmaking API Node Modules**: Ensure `node_modules/` in the `matchmaking-api` folder is ignored in Git. Members must run `npm install` inside the `matchmaking-api` directory on their own machines before running the API locally.
- [ ] **Firebase Admin SDK Key**: For the Node.js API to run locally on a team member's machine, they must have the Firebase Admin service account JSON file and configure their `GOOGLE_APPLICATION_CREDENTIALS` environment variable, or you must host it centrally (e.g., on Render) so they don't need to run it locally.
