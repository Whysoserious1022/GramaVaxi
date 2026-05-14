package com.gramavaxi.data.local.dao

import androidx.room.*
import com.gramavaxi.data.local.entity.AiCacheEntity
import com.gramavaxi.data.local.entity.OutbreakAlertEntity
import com.gramavaxi.data.local.entity.HealthRecordEntity
import com.gramavaxi.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: HealthRecordEntity)

    @Query("SELECT * FROM health_records WHERE animalId = :animalId ORDER BY recordDate DESC")
    fun getRecordsForAnimal(animalId: String): Flow<List<HealthRecordEntity>>

    @Query("SELECT * FROM health_records WHERE syncStatus = 'PENDING'")
    suspend fun getUnsyncedRecords(): List<HealthRecordEntity>
}

@Dao
interface OutbreakAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: OutbreakAlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<OutbreakAlertEntity>)

    @Query("SELECT * FROM outbreak_alerts ORDER BY reportedAt DESC")
    fun getAllAlerts(): Flow<List<OutbreakAlertEntity>>

    @Query("SELECT * FROM outbreak_alerts WHERE isRead = 0 ORDER BY reportedAt DESC")
    fun getUnreadAlerts(): Flow<List<OutbreakAlertEntity>>

    @Query("SELECT COUNT(*) FROM outbreak_alerts WHERE isRead = 0")
    fun getUnreadAlertCount(): Flow<Int>

    @Query("UPDATE outbreak_alerts SET isRead = 1 WHERE alertId = :alertId")
    suspend fun markAsRead(alertId: String)

    @Query("UPDATE outbreak_alerts SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE firebaseUid = :firebaseUid LIMIT 1")
    suspend fun getUserByFirebaseUid(firebaseUid: String): UserEntity?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
}

@Dao
interface AiCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: AiCacheEntity)

    @Query("SELECT * FROM ai_cache WHERE queryHash = :queryHash AND expiresAt > :now LIMIT 1")
    suspend fun getCachedResponse(queryHash: String, now: Long): AiCacheEntity?

    @Query("DELETE FROM ai_cache WHERE expiresAt < :now")
    suspend fun clearExpiredCache(now: Long)
}
