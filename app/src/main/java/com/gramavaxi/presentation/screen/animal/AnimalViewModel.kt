package com.gramavaxi.presentation.screen.animal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramavaxi.domain.model.Animal
import com.gramavaxi.domain.usecase.animal.GetAllAnimalsUseCase
import com.gramavaxi.domain.usecase.animal.RegisterAnimalUseCase
import com.gramavaxi.worker.WorkerScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class AnimalUiState {
    object Idle : AnimalUiState()
    object Loading : AnimalUiState()
    data class Success(val animal: Animal) : AnimalUiState()
    data class Error(val message: String) : AnimalUiState()
}

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val registerAnimalUseCase: RegisterAnimalUseCase,
    getAllAnimalsUseCase: GetAllAnimalsUseCase,
    private val workerScheduler: WorkerScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimalUiState>(AnimalUiState.Idle)
    val uiState: StateFlow<AnimalUiState> = _uiState.asStateFlow()

    val animals: StateFlow<List<Animal>> = getAllAnimalsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun registerAnimal(
        name: String,
        species: String,
        breed: String,
        ageMonths: Int,
        sex: String,
        ownerName: String,
        ownerPhone: String,
        village: String,
        district: String,
        notes: String
    ) {
        viewModelScope.launch {
            _uiState.value = AnimalUiState.Loading
            try {
                val animal = Animal(
                    animalId = UUID.randomUUID().toString(),
                    healthId = "",  // will be generated in use case
                    name = name.trim(),
                    species = species,
                    breed = breed.trim().ifEmpty { "Mixed" },
                    ageMonths = ageMonths,
                    sex = sex,
                    color = "",
                    weight = null,
                    photoUri = null,
                    ownerId = "current_user_id", // TODO: get from session
                    ownerName = ownerName.trim(),
                    ownerPhone = ownerPhone.trim(),
                    villageName = village.trim(),
                    district = district.trim(),
                    gpsLat = null,
                    gpsLng = null,
                    notes = notes.trim().ifEmpty { null },
                    createdAt = System.currentTimeMillis()
                )

                val result = registerAnimalUseCase(animal)
                result.fold(
                    onSuccess = { registeredAnimal ->
                        // Schedule periodic sync
                        workerScheduler.schedulePeriodicSync()
                        workerScheduler.schedulePeriodicHealthCheck()
                        _uiState.value = AnimalUiState.Success(registeredAnimal)
                    },
                    onFailure = { error ->
                        _uiState.value = AnimalUiState.Error(error.message ?: "Registration failed")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = AnimalUiState.Error(e.message ?: "Unexpected error")
            }
        }
    }

    fun resetState() {
        _uiState.value = AnimalUiState.Idle
    }
}
