package com.gramavaxi.data.repository

import com.gramavaxi.data.local.dao.OutbreakAlertDao
import com.gramavaxi.data.local.entity.OutbreakAlertEntity
import com.gramavaxi.domain.model.AlertSeverity
import com.gramavaxi.domain.model.OutbreakAlert
import com.gramavaxi.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val alertDao: OutbreakAlertDao
) : AlertRepository {

    override fun getAllAlerts(): Flow<List<OutbreakAlert>> =
        alertDao.getAllAlerts().map { it.map { e -> e.toDomain() } }

    override fun getUnreadAlerts(): Flow<List<OutbreakAlert>> =
        alertDao.getUnreadAlerts().map { it.map { e -> e.toDomain() } }

    override fun getUnreadAlertCount(): Flow<Int> = alertDao.getUnreadAlertCount()

    override suspend fun reportOutbreak(alert: OutbreakAlert): Result<Unit> {
        return try {
            alertDao.insertAlert(alert.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAlertAsRead(alertId: String) = alertDao.markAsRead(alertId)
    override suspend fun markAllAlertsAsRead() = alertDao.markAllAsRead()
    override suspend fun syncAlerts() { /* TODO: Firestore sync */ }

    private fun OutbreakAlertEntity.toDomain() = OutbreakAlert(
        alertId = alertId,
        alertType = alertType,
        disease = disease,
        severity = try { AlertSeverity.valueOf(severity) } catch (e: Exception) { AlertSeverity.LOW },
        title = title,
        description = description,
        actionRequired = actionRequired,
        reportedBy = reportedBy,
        reportedAt = reportedAt,
        isRead = isRead,
        isVerified = isVerified
    )

    private fun OutbreakAlert.toEntity() = OutbreakAlertEntity(
        alertId = alertId,
        alertType = alertType,
        disease = disease,
        severity = severity.name,
        affectedVillages = "[]",
        affectedSpecies = "[]",
        title = title,
        description = description,
        actionRequired = actionRequired,
        reportedBy = reportedBy,
        reportedByRole = "FARMER",
        reportedAt = reportedAt,
        expiresAt = null,
        isRead = isRead,
        isVerified = isVerified
    )
}
