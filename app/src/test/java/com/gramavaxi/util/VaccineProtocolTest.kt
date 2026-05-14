package com.gramavaxi.util

import com.gramavaxi.domain.model.Animal
import com.gramavaxi.domain.model.VaccinationSchedule
import com.gramavaxi.domain.model.VaccineStatus
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit

class VaccineProtocolTest {

    @Test
    fun testGenerateCattleSchedule() {
        val protocol = VaccineProtocol()
        val animal = Animal(
            animalId = "test_animal_1",
            healthId = "AHID-123",
            name = "Bessie",
            species = "Cow",
            breed = "Holstein",
            ageMonths = 2,
            sex = "Female",
            color = "Black/White",
            weight = null,
            photoUri = null,
            ownerId = "owner_1",
            ownerName = "John",
            ownerPhone = "1234567890",
            villageName = "Test Village",
            district = "KA",
            gpsLat = null,
            gpsLng = null,
            notes = null,
            createdAt = System.currentTimeMillis()
        )
        
        val schedules = protocol.generateSchedule(animal)

        assertTrue("Should have schedules generated", schedules.isNotEmpty())
        
        val fmdSchedule = schedules.find { it.vaccineName == "Foot and Mouth Disease (FMD)" }
        assertNotNull("FMD schedule should exist", fmdSchedule)
    }

    @Test
    fun testGenerateGoatSchedule() {
        val protocol = VaccineProtocol()
        val animal = Animal(
            animalId = "test_animal_2",
            healthId = "AHID-456",
            name = "Billy",
            species = "Goat",
            breed = "Boer",
            ageMonths = 3,
            sex = "Male",
            color = "White",
            weight = null,
            photoUri = null,
            ownerId = "owner_1",
            ownerName = "John",
            ownerPhone = "1234567890",
            villageName = "Test Village",
            district = "KA",
            gpsLat = null,
            gpsLng = null,
            notes = null,
            createdAt = System.currentTimeMillis()
        )
        
        val schedules = protocol.generateSchedule(animal)

        val pprSchedule = schedules.find { it.vaccineType == "PPR" }
        assertNotNull("PPR schedule should exist", pprSchedule)
    }
}
