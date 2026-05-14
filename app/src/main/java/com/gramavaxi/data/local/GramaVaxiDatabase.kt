package com.gramavaxi.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gramavaxi.data.local.dao.*
import com.gramavaxi.data.local.entity.*

@Database(
    entities = [
        AnimalEntity::class,
        VaccinationScheduleEntity::class,
        HealthRecordEntity::class,
        OutbreakAlertEntity::class,
        UserEntity::class,
        AiCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GramaVaxiDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun vaccinationDao(): VaccinationDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun outbreakAlertDao(): OutbreakAlertDao
    abstract fun userDao(): UserDao
    abstract fun aiCacheDao(): AiCacheDao

    companion object {
        const val DATABASE_NAME = "gramavaxi.db"
    }
}
