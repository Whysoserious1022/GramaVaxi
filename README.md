# Grama-Vaxi — AI-Powered Livestock Health & Vaccination App

## 🚀 Quick Start

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Firebase project setup
- Gemini API key (from Google AI Studio)

### Setup Steps

1. **Clone/open the project in Android Studio**
   - Open the `example/` folder as an Android project

2. **Add API Keys** in `gradle.properties`:
   ```
   GEMINI_API_KEY=your_actual_gemini_api_key
   MAPS_API_KEY=your_google_maps_api_key
   ```

3. **Firebase Setup**:
   - Go to https://console.firebase.google.com
   - Create a new project called "GramaVaxi"
   - Add Android app with package: `com.gramavaxi`
   - Download `google-services.json`
   - Place it at: `app/google-services.json`
   - Enable: Authentication (Phone), Firestore, Cloud Messaging

4. **Get Gemini API Key**:
   - Visit https://aistudio.google.com/app/apikey
   - Create an API key
   - Add to `gradle.properties`

5. **Build & Run**:
   ```
   Sync Gradle → Run on device/emulator
   ```

## 📁 Project Structure
```
app/src/main/java/com/gramavaxi/
├── GramaVaxiApp.kt          ← Hilt Application
├── MainActivity.kt          ← Entry point
├── di/                      ← Hilt DI Modules
├── data/
│   ├── local/               ← Room DB, entities, DAOs
│   ├── remote/              ← Firebase repositories
│   ├── ai/                  ← Gemini AI repository
│   └── repository/          ← Repository implementations
├── domain/
│   ├── model/               ← Domain models
│   ├── repository/          ← Repository interfaces
│   └── usecase/             ← Business logic use cases
├── presentation/
│   ├── navigation/          ← NavGraph + Screen routes
│   ├── theme/               ← Colors, Typography, Theme
│   └── screen/              ← All UI screens + ViewModels
├── worker/                  ← WorkManager workers
├── notification/            ← NotificationHelper + FCM
└── util/                    ← VaccineProtocol, Extensions
```

## 🔑 Key Features Implemented
- [x] MVVM + Clean Architecture
- [x] Room DB (offline-first)
- [x] Gemini AI Chatbot (Kannada)
- [x] Voice Input (Kannada STT)
- [x] WorkManager vaccine reminders
- [x] Firebase FCM push notifications
- [x] Multi-language (EN + KN)
- [x] Animal registration with auto vaccine scheduling
- [x] Vaccination logging
- [x] Outbreak alerts system

## 🔧 Next Steps
- [ ] Firebase Authentication (Phone OTP)
- [ ] Full Firestore sync implementation
- [ ] CameraX animal photo capture
- [ ] Google Maps vet finder
- [ ] QR code health card PDF export
- [ ] MPAndroidChart analytics
- [ ] Gemini Vision disease detection
- [ ] Admin/Vet portal screens

## 📝 Notes
- This file (`google-services.json`) is NOT committed — add your own
- Never commit API keys
- Min SDK: 26 (Android 8.0)
