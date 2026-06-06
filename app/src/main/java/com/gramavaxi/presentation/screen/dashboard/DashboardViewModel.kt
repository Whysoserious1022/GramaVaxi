package com.gramavaxi.presentation.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.gramavaxi.domain.model.Animal
import com.gramavaxi.domain.model.VaccinationSchedule
import com.gramavaxi.domain.repository.UserRepository
import com.gramavaxi.domain.usecase.animal.GetAllAnimalsUseCase
import com.gramavaxi.domain.usecase.vaccine.GetUpcomingVaccinationsUseCase
import com.gramavaxi.domain.usecase.vaccine.GetOverdueVaccinationsUseCase
import com.gramavaxi.domain.usecase.vaccine.LogVaccinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val animals: List<Animal> = emptyList(),
    val upcomingVaccinations: List<VaccinationSchedule> = emptyList(),
    val overdueVaccinations: List<VaccinationSchedule> = emptyList(),
    val unreadAlertCount: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val userName: String = "Farmer",       // Fetched from Firestore
    val userPhotoUrl: String = ""
) {
    val totalAnimals get() = animals.size
    val healthyAnimals get() = animals.size - overdueVaccinations.map { it.animalId }.distinct().size
    val animalsNeedingAttention get() = overdueVaccinations.map { it.animalId }.distinct().size
    val fullyVaccinated get() = healthyAnimals
    val firstName get() = userName.trim().split(" ").firstOrNull() ?: "Farmer"
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getAllAnimalsUseCase: GetAllAnimalsUseCase,
    private val getUpcomingVaccinationsUseCase: GetUpcomingVaccinationsUseCase,
    private val getOverdueVaccinationsUseCase: GetOverdueVaccinationsUseCase,
    private val logVaccinationUseCase: LogVaccinationUseCase,
    private val userRepository: UserRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
        loadUserProfile()
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            combine(
                getAllAnimalsUseCase(),
                getUpcomingVaccinationsUseCase(7)
            ) { animals, upcoming ->
                _uiState.update { it.copy(animals = animals, upcomingVaccinations = upcoming, isLoading = false) }
            }.catch { e ->
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }.collect()
        }

        viewModelScope.launch {
            try {
                val overdue = getOverdueVaccinationsUseCase()
                _uiState.update { it.copy(overdueVaccinations = overdue) }
            } catch (_: Exception) {}
        }
    }

    private fun loadUserProfile() {
        val uid = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            // Immediately use Firebase Auth display name as a fast fallback
            val displayName = firebaseAuth.currentUser?.displayName ?: ""
            if (displayName.isNotEmpty()) {
                _uiState.update { it.copy(userName = displayName) }
            }
            // Then subscribe to Firestore real-time updates
            userRepository.getUserProfile(uid)
                .catch { /* non-fatal */ }
                .collect { profile ->
                    if (profile != null && profile.name.isNotEmpty()) {
                        _uiState.update {
                            it.copy(userName = profile.name, userPhotoUrl = profile.photoUrl)
                        }
                    }
                }
        }
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadDashboard()
    }

    fun updateProfilePhoto(photoUrl: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.updatePhotoUrl(uid, photoUrl)
            _uiState.update { it.copy(userPhotoUrl = photoUrl) }
        }
    }

    fun markVaccinationDone(scheduleId: String) {
        viewModelScope.launch {
            try {
                logVaccinationUseCase(
                    scheduleId = scheduleId,
                    completedDate = System.currentTimeMillis(),
                    batchNumber = null,
                    notes = "Marked done from dashboard"
                )
                // Refresh overdue list
                val overdue = getOverdueVaccinationsUseCase()
                _uiState.update { it.copy(overdueVaccinations = overdue) }
            } catch (_: Exception) {}
        }
    }
}
