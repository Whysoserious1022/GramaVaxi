package com.gramavaxi.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gramavaxi.presentation.screen.ProfileScreen
import com.gramavaxi.presentation.screen.ai.AIChatScreen
import com.gramavaxi.presentation.screen.ai.DiseaseDetectionScreen
import com.gramavaxi.presentation.screen.ai.VoiceAssistantScreen
import com.gramavaxi.presentation.screen.alert.AlertsScreen
import com.gramavaxi.presentation.screen.alert.ReportOutbreakScreen
import com.gramavaxi.presentation.screen.analytics.AnalyticsDashboardScreen
import com.gramavaxi.presentation.screen.animal.AnimalListScreen
import com.gramavaxi.presentation.screen.animal.AnimalProfileScreen
import com.gramavaxi.presentation.screen.animal.RegisterAnimalScreen
import com.gramavaxi.presentation.screen.dashboard.DashboardScreen
import com.gramavaxi.presentation.screen.map.NearbyVetScreen
import com.gramavaxi.presentation.screen.onboarding.AuthViewModel
import com.gramavaxi.presentation.screen.onboarding.LanguageSelectScreen
import com.gramavaxi.presentation.screen.onboarding.LoginScreen
import com.gramavaxi.presentation.screen.onboarding.SignUpScreen
import com.gramavaxi.presentation.screen.onboarding.SplashScreen
import com.gramavaxi.presentation.screen.vaccine.LogVaccinationScreen
import com.gramavaxi.presentation.screen.vaccine.VaccinationCalendarScreen
import com.gramavaxi.presentation.screen.healthcard.DigitalHealthCardScreen

// Routes where the bottom nav should be visible
private val bottomNavRoutes = setOf(
    Screen.Dashboard.route,
    Screen.AnimalList.route,
    Screen.VaccinationCalendar.route,
    Screen.Profile.route
)

@Composable
fun GramaVaxiNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val showBottomNav = bottomNavRoutes.any { currentRoute?.startsWith(it.substringBefore("{")) == true }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                GramaVaxiBottomNavBar(
                    navController = navController,
                    onAIClick = { navController.navigate(Screen.AIChat.route) }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // ── Onboarding ────────────────────────────────────────────────
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    },
                    onNavigateToDashboard = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.LanguageSelect.route) {
                LanguageSelectScreen(
                    onLanguageSelected = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    onNavigateToSignUp = {
                        navController.navigate(Screen.Register.route)
                    },
                    viewModel = authViewModel
                )
            }
            
            composable(Screen.Register.route) {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    viewModel = authViewModel
                )
            }

            // ── Dashboard ─────────────────────────────────────────────────
            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    onNavigateToRegisterAnimal = { navController.navigate(Screen.RegisterAnimal.route) },
                    onNavigateToAnimalList     = { navController.navigate(Screen.AnimalList.route) },
                    onNavigateToAIChat         = { navController.navigate(Screen.AIChat.route) },
                    onNavigateToAlerts         = { navController.navigate(Screen.Alerts.route) },
                    onNavigateToNearbyVet      = { navController.navigate(Screen.NearbyVet.route) },
                    onNavigateToAnalytics      = { navController.navigate(Screen.Analytics.route) },
                    onNavigateToAnimalProfile  = { id -> navController.navigate(Screen.AnimalProfile.createRoute(id)) }
                )
            }

            // ── Profile ───────────────────────────────────────────────────
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSignOut = {
                        authViewModel.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    onNavigateToAlerts = { navController.navigate(Screen.Alerts.route) }
                )
            }

            // ── Animal ────────────────────────────────────────────────────
            composable(Screen.AnimalList.route) {
                AnimalListScreen(
                    onAnimalClick = { id -> navController.navigate(Screen.AnimalProfile.createRoute(id)) },
                    onAddAnimal   = { navController.navigate(Screen.RegisterAnimal.route) },
                    onBack        = { navController.popBackStack() }
                )
            }

            composable(Screen.RegisterAnimal.route) {
                RegisterAnimalScreen(
                    onAnimalRegistered = { animalId ->
                        navController.navigate(Screen.AnimalProfile.createRoute(animalId)) {
                            popUpTo(Screen.RegisterAnimal.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.AnimalProfile.route,
                arguments = listOf(navArgument("animalId") { type = NavType.StringType })
            ) { backStackEntry ->
                val animalId = backStackEntry.arguments?.getString("animalId") ?: return@composable
                AnimalProfileScreen(
                    animalId                 = animalId,
                    onNavigateToCalendar     = { navController.navigate(Screen.VaccinationCalendar.createRoute(animalId)) },
                    onNavigateToHealthCard   = { navController.navigate(Screen.DigitalHealthCard.createRoute(animalId)) },
                    onNavigateToAIChat       = { navController.navigate(Screen.AIChat.route) },
                    onNavigateToDiseaseDetection = { navController.navigate(Screen.DiseaseDetection.route) },
                    onBack                   = { navController.popBackStack() }
                )
            }

            // ── Vaccine ───────────────────────────────────────────────────
            composable(
                route = Screen.VaccinationCalendar.route,
                arguments = listOf(navArgument("animalId") { type = NavType.StringType })
            ) { backStackEntry ->
                val animalId = backStackEntry.arguments?.getString("animalId") ?: return@composable
                VaccinationCalendarScreen(
                    animalId        = animalId,
                    onLogVaccination = { scheduleId -> navController.navigate(Screen.LogVaccination.createRoute(scheduleId)) },
                    onBack          = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.LogVaccination.route,
                arguments = listOf(navArgument("scheduleId") { type = NavType.StringType })
            ) { backStackEntry ->
                val scheduleId = backStackEntry.arguments?.getString("scheduleId") ?: return@composable
                LogVaccinationScreen(
                    scheduleId = scheduleId,
                    onLogged   = { navController.popBackStack() },
                    onBack     = { navController.popBackStack() }
                )
            }

            // ── AI ────────────────────────────────────────────────────────
            composable(Screen.AIChat.route) {
                AIChatScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.DiseaseDetection.route) {
                DiseaseDetectionScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.VoiceAssistant.route) {
                VoiceAssistantScreen(onBack = { navController.popBackStack() })
            }

            // ── Alerts ────────────────────────────────────────────────────
            composable(Screen.Alerts.route) {
                AlertsScreen(
                    onReportOutbreak = { navController.navigate(Screen.ReportOutbreak.route) },
                    onBack           = { navController.popBackStack() }
                )
            }

            composable(Screen.ReportOutbreak.route) {
                ReportOutbreakScreen(
                    onReported = { navController.popBackStack() },
                    onBack     = { navController.popBackStack() }
                )
            }

            // ── Map ───────────────────────────────────────────────────────
            composable(Screen.NearbyVet.route) {
                NearbyVetScreen(onBack = { navController.popBackStack() })
            }

            // ── Analytics ─────────────────────────────────────────────────
            composable(Screen.Analytics.route) {
                AnalyticsDashboardScreen(onBack = { navController.popBackStack() })
            }

            // ── Health Card ───────────────────────────────────────────────
            composable(
                route = Screen.DigitalHealthCard.route,
                arguments = listOf(navArgument("animalId") { type = NavType.StringType })
            ) { backStackEntry ->
                val animalId = backStackEntry.arguments?.getString("animalId") ?: return@composable
                DigitalHealthCardScreen(
                    animalId = animalId,
                    onBack   = { navController.popBackStack() }
                )
            }
        }
    }
}
