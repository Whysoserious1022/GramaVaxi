package com.gramavaxi.util

import com.gramavaxi.domain.model.*
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaccineProtocol @Inject constructor() {

    /**
     * Government-standard vaccination protocol for each species.
     * Based on NADCP and Karnataka livestock department guidelines.
     */
    fun generateSchedule(animal: Animal): List<VaccinationSchedule> {
        val schedules = mutableListOf<VaccinationSchedule>()
        val now = System.currentTimeMillis()

        when (animal.species.uppercase()) {
            "COW", "BUFFALO" -> {
                schedules.addAll(generateCattleSchedule(animal, now))
            }
            "GOAT", "SHEEP" -> {
                schedules.addAll(generateSmallRuminantSchedule(animal, now))
            }
            "PIG" -> {
                schedules.addAll(generatePigSchedule(animal, now))
            }
        }

        return schedules
    }

    private fun generateCattleSchedule(animal: Animal, now: Long): List<VaccinationSchedule> {
        return listOf(
            // FMD - Every 6 months
            createSchedule(animal, "Foot and Mouth Disease (FMD)", "FMD", 1, 2, daysFromNow(0, now)),
            createSchedule(animal, "Foot and Mouth Disease (FMD) - Booster", "FMD", 2, 2, daysFromNow(180, now)),

            // Hemorrhagic Septicemia - Annual
            createSchedule(animal, "Hemorrhagic Septicemia (HS)", "HS", 1, 1, daysFromNow(30, now)),

            // Black Quarter - Annual (cattle only)
            createSchedule(animal, "Black Quarter (BQ)", "BQ", 1, 1, daysFromNow(45, now)),

            // Brucellosis (heifers 4-8 months only)
            if (animal.sex == "Female" && animal.ageMonths in 4..8) {
                createSchedule(animal, "Brucellosis", "BRUCELLA", 1, 1, daysFromNow(7, now))
            } else null,

            // Theileriosis (Tropical areas)
            createSchedule(animal, "Theileriosis", "THEILERIA", 1, 1, daysFromNow(60, now)),
        ).filterNotNull()
    }

    private fun generateSmallRuminantSchedule(animal: Animal, now: Long): List<VaccinationSchedule> {
        return listOf(
            // PPR - Small ruminants
            createSchedule(animal, "Peste des Petits Ruminants (PPR)", "PPR", 1, 1, daysFromNow(0, now)),

            // Enterotoxemia
            createSchedule(animal, "Enterotoxemia", "ENTERO", 1, 2, daysFromNow(14, now)),
            createSchedule(animal, "Enterotoxemia - Booster", "ENTERO", 2, 2, daysFromNow(44, now)),

            // FMD for goats
            createSchedule(animal, "Foot and Mouth Disease (FMD)", "FMD", 1, 2, daysFromNow(30, now)),
            createSchedule(animal, "Foot and Mouth Disease (FMD) - Booster", "FMD", 2, 2, daysFromNow(210, now)),
        )
    }

    private fun generatePigSchedule(animal: Animal, now: Long): List<VaccinationSchedule> {
        return listOf(
            createSchedule(animal, "Swine Fever (Classical)", "SWINE_FEVER", 1, 1, daysFromNow(0, now)),
            createSchedule(animal, "Foot and Mouth Disease (FMD)", "FMD", 1, 2, daysFromNow(21, now)),
        )
    }

    private fun createSchedule(
        animal: Animal,
        vaccineName: String,
        vaccineType: String,
        doseNumber: Int,
        totalDoses: Int,
        dueDate: Long
    ): VaccinationSchedule {
        return VaccinationSchedule(
            scheduleId = UUID.randomUUID().toString(),
            animalId = animal.animalId,
            animalName = animal.name,
            vaccineName = vaccineName,
            vaccineType = vaccineType,
            doseNumber = doseNumber,
            totalDoses = totalDoses,
            dueDate = dueDate,
            completedDate = null,
            status = VaccineStatus.PENDING,
            notes = null
        )
    }

    private fun daysFromNow(days: Int, now: Long): Long {
        return now + (days * 24 * 60 * 60 * 1000L)
    }
}

fun generateHealthId(district: String): String {
    val year = Calendar.getInstance().get(Calendar.YEAR)
    val districtCode = district.take(2).uppercase()
    val random = (100000..999999).random()
    return "AHID-$year-$districtCode-$random"
}
