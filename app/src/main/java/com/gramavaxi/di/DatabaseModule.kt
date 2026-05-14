package com.gramavaxi.di

import android.content.Context
import androidx.room.Room
import com.gramavaxi.data.local.GramaVaxiDatabase
import com.gramavaxi.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GramaVaxiDatabase {
        return Room.databaseBuilder(
            context,
            GramaVaxiDatabase::class.java,
            GramaVaxiDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideAnimalDao(db: GramaVaxiDatabase): AnimalDao = db.animalDao()

    @Provides
    fun provideVaccinationDao(db: GramaVaxiDatabase): VaccinationDao = db.vaccinationDao()

    @Provides
    fun provideHealthRecordDao(db: GramaVaxiDatabase): HealthRecordDao = db.healthRecordDao()

    @Provides
    fun provideOutbreakAlertDao(db: GramaVaxiDatabase): OutbreakAlertDao = db.outbreakAlertDao()

    @Provides
    fun provideUserDao(db: GramaVaxiDatabase): UserDao = db.userDao()

    @Provides
    fun provideAiCacheDao(db: GramaVaxiDatabase): AiCacheDao = db.aiCacheDao()
}
