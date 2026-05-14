package com.gramavaxi.domain.model

data class Animal(
    val animalId: String,
    val healthId: String,
    val name: String,
    val species: String,
    val breed: String,
    val ageMonths: Int,
    val sex: String,
    val color: String,
    val weight: Float?,
    val photoUri: String?,
    val ownerId: String,
    val ownerName: String,
    val ownerPhone: String,
    val villageName: String,
    val district: String,
    val gpsLat: Double?,
    val gpsLng: Double?,
    val notes: String?,
    val createdAt: Long
)

data class VaccinationSchedule(
    val scheduleId: String,
    val animalId: String,
    val animalName: String,
    val vaccineName: String,
    val vaccineType: String,
    val doseNumber: Int,
    val totalDoses: Int,
    val dueDate: Long,
    val completedDate: Long?,
    val status: VaccineStatus,
    val notes: String?
)

enum class VaccineStatus { PENDING, COMPLETED, OVERDUE, SKIPPED }

data class HealthRecord(
    val recordId: String,
    val animalId: String,
    val recordDate: Long,
    val symptoms: String?,
    val diagnosis: String?,
    val treatment: String?,
    val vetName: String?,
    val aiSuggestion: String?,
    val urgencyLevel: UrgencyLevel?
)

enum class UrgencyLevel { LOW, MEDIUM, HIGH, EMERGENCY }

data class OutbreakAlert(
    val alertId: String,
    val alertType: String,
    val disease: String,
    val severity: AlertSeverity,
    val title: String,
    val description: String,
    val actionRequired: String?,
    val reportedBy: String,
    val reportedAt: Long,
    val isRead: Boolean,
    val isVerified: Boolean
)

enum class AlertSeverity { LOW, MEDIUM, HIGH, CRITICAL }

data class User(
    val userId: String,
    val firebaseUid: String,
    val name: String,
    val phone: String,
    val role: UserRole,
    val villageName: String,
    val district: String,
    val preferredLanguage: String
)

enum class UserRole { FARMER, VET, FIELD_OFFICER, ADMIN }

data class ChatMessage(
    val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long,
    val isLoading: Boolean = false
)

data class DiseaseAnalysis(
    val likelyDiseases: List<String>,
    val confidence: Float,
    val urgency: UrgencyLevel,
    val immediateActions: List<String>,
    val prevention: List<String>,
    val vetRequired: Boolean,
    val rawResponse: String
)
