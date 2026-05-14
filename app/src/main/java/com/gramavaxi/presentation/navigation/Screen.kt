package com.gramavaxi.presentation.navigation

sealed class Screen(val route: String) {
    // Onboarding
    object Splash : Screen("splash")
    object LanguageSelect : Screen("language_select")
    object Login : Screen("login")
    object Register : Screen("register")

    // Main
    object Dashboard : Screen("dashboard")

    // Animal
    object AnimalList : Screen("animal_list")
    object RegisterAnimal : Screen("register_animal")
    object AnimalProfile : Screen("animal_profile/{animalId}") {
        fun createRoute(animalId: String) = "animal_profile/$animalId"
    }

    // Vaccine
    object VaccinationCalendar : Screen("vaccination_calendar/{animalId}") {
        fun createRoute(animalId: String) = "vaccination_calendar/$animalId"
    }
    object LogVaccination : Screen("log_vaccination/{scheduleId}") {
        fun createRoute(scheduleId: String) = "log_vaccination/$scheduleId"
    }

    // AI
    object AIChat : Screen("ai_chat")
    object DiseaseDetection : Screen("disease_detection")
    object VoiceAssistant : Screen("voice_assistant")

    // Alerts
    object Alerts : Screen("alerts")
    object ReportOutbreak : Screen("report_outbreak")

    // Map
    object NearbyVet : Screen("nearby_vet")

    // Analytics
    object Analytics : Screen("analytics")

    // Settings
    object Settings : Screen("settings")
    object Profile  : Screen("profile")
    object DigitalHealthCard : Screen("health_card/{animalId}") {
        fun createRoute(animalId: String) = "health_card/$animalId"
    }
}
