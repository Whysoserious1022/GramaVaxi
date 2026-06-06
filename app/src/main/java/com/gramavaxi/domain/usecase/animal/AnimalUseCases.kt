package com.gramavaxi.domain.usecase.animal

import com.gramavaxi.domain.model.Animal
import com.gramavaxi.domain.repository.AnimalRepository
import com.gramavaxi.domain.repository.VaccinationRepository
import com.gramavaxi.util.VaccineProtocol
import com.gramavaxi.util.generateHealthId
import com.gramavaxi.worker.WorkerScheduler
import java.util.UUID
import javax.inject.Inject

class RegisterAnimalUseCase @Inject constructor(
    private val animalRepository: AnimalRepository,
    private val vaccinationRepository: VaccinationRepository,
    private val vaccineProtocol: VaccineProtocol,
    private val workerScheduler: WorkerScheduler
) {
    suspend operator fun invoke(animal: Animal): Result<Animal> {
        val animalWithId = animal.copy(
            animalId = animal.animalId.ifEmpty { UUID.randomUUID().toString() },
            healthId = animal.healthId.ifEmpty { generateHealthId(animal.district) }
        )

        val animalResult = animalRepository.registerAnimal(animalWithId)
        if (animalResult.isFailure) return animalResult

        // Generate vaccine schedule based on species and age
        val schedules = vaccineProtocol.generateSchedule(animalWithId)
        val scheduleResult = vaccinationRepository.saveSchedules(schedules)
        if (scheduleResult.isSuccess) {
            schedules.forEach { workerScheduler.scheduleVaccinationReminders(it) }
        }

        return Result.success(animalWithId)
    }
}

class GetAnimalsForOwnerUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    operator fun invoke(ownerId: String) = repository.getAnimalsForOwner(ownerId)
}

class GetAllAnimalsUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    operator fun invoke() = repository.getAllAnimals()
}

class GetAnimalByIdUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    suspend operator fun invoke(animalId: String) = repository.getAnimalById(animalId)
}

class UpdateAnimalPhotoUseCase @Inject constructor(
    private val repository: AnimalRepository
) {
    suspend operator fun invoke(animalId: String, photoUri: String): Result<Unit> {
        val animal = repository.getAnimalById(animalId) ?: return Result.failure(Exception("Animal not found"))
        val updatedAnimal = animal.copy(photoUri = photoUri)
        return repository.registerAnimal(updatedAnimal).map {}
    }
}
