package com.gramavaxi.presentation.screen.vaccine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramavaxi.domain.model.VaccinationSchedule
import com.gramavaxi.domain.model.VaccineStatus
import com.gramavaxi.domain.repository.VaccinationRepository
import com.gramavaxi.domain.usecase.vaccine.GetSchedulesForAnimalUseCase
import com.gramavaxi.domain.usecase.vaccine.LogVaccinationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class LogVaccineUiState {
    object Idle : LogVaccineUiState()
    object Loading : LogVaccineUiState()
    object Success : LogVaccineUiState()
    data class Error(val message: String) : LogVaccineUiState()
}

@HiltViewModel
class VaccinationViewModel @Inject constructor(
    private val getSchedulesForAnimalUseCase: GetSchedulesForAnimalUseCase,
    private val logVaccinationUseCase: LogVaccinationUseCase,
    private val vaccinationRepository: VaccinationRepository
) : ViewModel() {

    private val _logUiState = MutableStateFlow<LogVaccineUiState>(LogVaccineUiState.Idle)
    val logUiState: StateFlow<LogVaccineUiState> = _logUiState.asStateFlow()

    fun getSchedulesForAnimal(animalId: String) = getSchedulesForAnimalUseCase(animalId)

    fun logVaccination(scheduleId: String, batchNumber: String, notes: String) {
        viewModelScope.launch {
            _logUiState.value = LogVaccineUiState.Loading
            val result = logVaccinationUseCase(
                scheduleId = scheduleId,
                completedDate = System.currentTimeMillis(),
                batchNumber = batchNumber.ifBlank { null },
                notes = notes.ifBlank { null }
            )
            _logUiState.value = result.fold(
                onSuccess = { LogVaccineUiState.Success },
                onFailure = { LogVaccineUiState.Error(it.message ?: "Failed") }
            )
        }
    }

    fun addCustomEvent(animalId: String, animalName: String, eventName: String, date: Long, notes: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val schedule = VaccinationSchedule(
                scheduleId = UUID.randomUUID().toString(),
                animalId = animalId,
                animalName = animalName,
                vaccineName = eventName,
                vaccineType = "Custom",
                doseNumber = 1,
                totalDoses = 1,
                dueDate = date,
                completedDate = null,
                status = VaccineStatus.PENDING,
                notes = notes.ifBlank { null }
            )
            val result = vaccinationRepository.saveSchedules(listOf(schedule))
            if (result.isFailure) {
                android.util.Log.e("VaccinationViewModel", "Failed to add event: ${result.exceptionOrNull()?.message}")
            }
            onSuccess()
        }
    }
}
