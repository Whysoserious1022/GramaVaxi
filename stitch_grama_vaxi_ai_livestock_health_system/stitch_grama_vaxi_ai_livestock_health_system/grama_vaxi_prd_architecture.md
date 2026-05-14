# Grama-Vaxi: AI-Powered Livestock Health & Vaccination System

## Project Overview
Grama-Vaxi is a production-level Android application designed to digitize livestock healthcare for rural farmers. It leverages AI (Gemini) for disease prediction, WorkManager for offline-first vaccination reminders, and Google Maps for camp tracking.

## Technical Architecture (Android)
- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** Clean Architecture + MVVM
- **Local DB:** Room (Offline-first)
- **Backend:** Firebase (Auth, Firestore, Storage, FCM)
- **AI Integration:** Google Gemini SDK for health assistance
- **Background Tasks:** WorkManager for reliable scheduling

## Screen List & User Flow
1. **Splash & Onboarding:** Branding and multi-language selection (Kannada/English).
2. **Authentication:** Phone OTP & Google Sign-in.
3. **Dashboard:** Central hub with Health Analytics, upcoming vaccinations, and AI shortcuts.
4. **Animal Registration:** Multi-step form with photo upload and QR generation.
5. **Animal Details:** Digital Health Card, vaccination history, and individual health trends.
6. **Vaccine Calendar:** Smart scheduler with color-coded statuses (Green/Yellow/Red).
7. **AI Assistant (Grama-Mitra):** Voice-enabled Gemini chat for livestock care.
8. **Disease Reporting:** Symptom selector with AI-preliminary suggestions.
9. **Camp Locator:** Map view of nearby veterinary hospitals and camps.
10. **Profile & Settings:** Village details and animal count management.

## Database Structure (Firestore)
- **Users:** `uid, name, village, district, language, phone`
- **Animals:** `animalId, ownerId, type, breed, age, weight, qrCodeUrl, photoUrl`
- **Vaccinations:** `vaccineId, animalId, name, dateAdministered, dueDate, status`
- **Reports:** `reportId, userId, symptoms, photoUrl, aiSuggestion, status`