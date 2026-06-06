# Grama-Vaxi (ಗ್ರಾಮ-ವ್ಯಾಕ್ಸಿ) — AI-Powered Livestock Health & Vaccination App

Grama-Vaxi is a comprehensive, offline-first Android application designed to digitize livestock management, streamline vaccination schedules, and empower rural farmers with advanced AI diagnostics in their native language (Kannada & English). 

Developed as part of the Computer Science & Engineering (AI & ML) curriculum.

---

## 👨‍💻 Developer Information
* **Name:** Dayananda S G
* **USN:** 1GA22CI015
* **Email:** dayananda1ga22ci015@gmail.com
* **Department:** Computer Science & Engineering (AI & ML)

---

## 🚀 Key Features Implemented

### 1. 📲 Core Onboarding & Security
* **Authentication:** Secure user sign-up and login with profile management.
* **Language Selector:** Seamless onboarding support for **English** and **Kannada (ಕನ್ನಡ)** with persistent preferences.

### 2. 🐮 Livestock & Vaccination Management
* **Animal Registration:** Register animals (Cattle/Cow, Goat, Sheep, Buffalo, etc.) with village-level metadata.
* **Smart Vaccine Protocol:** Automated generation of vaccination schedules based on animal species and age.
* **Vaccination Logging:** Log administered vaccines with dose tracking.
* **Interactive Calendar:** Visual schedule calendar for upcoming, completed, and overdue vaccinations.
* **Reminders & Alerts:** Custom vaccine reminder notifications powered by Jetpack WorkManager and Firebase Cloud Messaging (FCM).

### 3. 🤖 Grama AI Assistance
* **Text AI Chatbot:** Gemini AI-powered assistant for animal health recommendations (fully supports Kannada query processing).
* **Kannada Voice Assistant:** Hands-free Kannada Speech-to-Text (STT) input for busy farmers.
* **Visual AI Diagnosis:** Gemini Vision UI enabling farmers to upload/capture photos of visible symptoms (skin, eyes, hooves) for instant diagnostic guidance.

### 4. 🗺️ Interactive Maps & Vet Finder
* **Karnataka Vet Hospital Directory:** Real-world GPS mapping of Karnataka Government Veterinary Hospitals (Tumkur, Kunigal, Mysuru, Hassan, Mandya, etc.).
* **Directions:** One-tap navigation from the map to Google Maps.
* **Live Animal Overlay:** Visualization of registered livestock coordinates colored by species.

### 5. 📊 Analytics Dashboard
* **Herd Health Overview:** Real-time metrics on healthy vs. overdue vs. upcoming vaccinations.
* **Species Distribution:** Visual progress bars depicting the ratio of cattle, goats, buffaloes, and other livestock.
* **District-Level Coverage:** Distribution analysis of registered animals across districts.

### 6. 🧾 Digital Health Cards & Outbreak Reporting
* **Digital Health Cards:** Displays individual animal details and ID with QR-profile placeholders.
* **Outbreak Alerts:** Report and monitor local disease outbreaks by district to prevent contagions.

---

## 🛠️ Technology Stack
* **Architecture:** MVVM + Clean Architecture (Presentation, Domain, Data layers)
* **UI Framework:** Jetpack Compose (Modern, responsive, and animated UI)
* **Local Storage:** Room Database (Offline-first architecture)
* **Remote Sync:** Firebase Cloud Firestore & Authentication
* **Dependency Injection:** Hilt
* **AI Engine:** Google Gemini SDK (Text-only + Multi-modal Vision)
* **Background Processing:** WorkManager (Scheduling notifications)
* **Map API:** Google Maps Compose SDK
* **Speech Processing:** SpeechRecognizer (Android STT)

---

## 📁 Project Structure
```
app/src/main/java/com/gramavaxi/
├── GramaVaxiApp.kt          ← Hilt Application
├── MainActivity.kt          ← Entry point with Locale & Permission helper
├── di/                      ← Dependency Injection modules (Network, AI, Database)
├── data/
│   ├── local/               ← Room Database configurations, DAOs, and Entities
│   ├── remote/              ← Firebase Firestore and Auth repository implementations
│   ├── ai/                  ← Gemini API client configuration
│   └── repository/          ← Local & Remote repository coordination
├── domain/
│   ├── model/               ← Core business logic data models (Animal, UserProfile, etc.)
│   ├── repository/          ← Data layer abstractions/interfaces
│   └── usecase/             ← Business rule interactors (Animal & Vaccination business logic)
├── presentation/
│   ├── navigation/          ← NavHost, routes, and custom bottom navigation bar
│   ├── theme/               ← App design tokens (Colors, Typography, Themes)
│   ├── util/                ← UI helper extensions and text formatters
│   └── screen/              ← All ViewModels, Composables, and states grouped by feature:
│       ├── ai/              ← AIChat, DiseaseDetection, and VoiceAssistant screens
│       ├── alert/           ← Outbreak reporting and Alerts list screens
│       ├── analytics/       ← Custom analytics charts and progress indicators
│       ├── animal/          ← Registration, List, and Animal Profile screens
│       ├── dashboard/       ← Home landing screen with shortcut actions
│       ├── healthcard/      ← Digital QR Health Card screen
│       ├── map/             ← Google Map vet clinic finder screen
│       ├── onboarding/      ← Splash, Language Select, Login, and Signup screens
│       └── vaccine/         ← LogVaccination and VaccinationCalendar screens
├── worker/                  ← Jetpack WorkManager workers for scheduled push notifications
├── notification/            ← Firebase FCM handlers and local Notification channel builders
└── util/                    ← LanguageManager, VaccineProtocol, and formatting helper classes
```

---

## 🚀 Quick Setup & Installation

### Prerequisites
* **IDE:** Android Studio Hedgehog (2023.1.1) or later
* **JDK:** Version 17+
* **Min SDK:** 26 (Android 8.0)

### Setup Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Whysoserious1022/GramaVaxi.git
   ```
2. **Open the project:**
   Open the `example/` folder directly in Android Studio.
3. **Configure API Keys:**
   Create/update `gradle.properties` in your root directory and add:
   ```properties
   GEMINI_API_KEY=your_actual_gemini_api_key
   MAPS_API_KEY=your_google_maps_api_key
   ```
4. **Setup Firebase:**
   * Go to the [Firebase Console](https://console.firebase.google.com).
   * Create a project named `GramaVaxi`.
   * Register an Android app with the package name `com.gramavaxi`.
   * Download `google-services.json` and place it in the `app/` folder.
   * Enable **Email/Password or Phone Authentication**, **Cloud Firestore**, and **Cloud Messaging (FCM)**.
5. **Build and Run:**
   Sync Gradle and run the app on a physical device or emulator.

---

## 📈 Testing & Optimizations (From PDF Report)
To ensure rural applicability and high performance, the following testing and optimization procedures were executed:

### Testing Highlights
* **Functional Validation:** Rigorously tested registration protocols, vaccination alert scheduling, Room DB operations, and Firebase sync.
* **Offline Usability:** Confirmed full functionality (registration, log viewing, and local search) without an internet connection.
* **Performance Check:** Validated low battery consumption, prompt notification triggers via WorkManager, and rapid database response times.

### Optimization Highlights
* **Database Queries:** Optimized index usage and write transactions in Room.
* **UI Performance:** Improved Compose List layout rendering speed and resolved image rendering lag.
* **Locale Management:** Implemented automatic local state caching to guarantee instantaneous language rendering without restart latency.

---

## 🔮 Future Enhancements
- [ ] Direct Firebase Phone OTP Verification.
- [ ] Local Offline PDF generation for QR Health Cards.
- [ ] Integration of custom TensorFlow Lite models for local offline disease classification.
- [ ] Real-time GPS distance calculation to the nearest vet clinic.
