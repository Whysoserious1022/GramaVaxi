package com.gramavaxi.di

import com.gramavaxi.data.repository.AlertRepositoryImpl
import com.gramavaxi.data.repository.AnimalRepositoryImpl
import com.gramavaxi.data.repository.VaccinationRepositoryImpl
import com.gramavaxi.domain.repository.AlertRepository
import com.gramavaxi.domain.repository.AnimalRepository
import com.gramavaxi.domain.repository.VaccinationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindAnimalRepository(impl: AnimalRepositoryImpl): AnimalRepository

    @Binds
    @Singleton
    abstract fun bindVaccinationRepository(impl: VaccinationRepositoryImpl): VaccinationRepository

    @Binds
    @Singleton
    abstract fun bindAlertRepository(impl: AlertRepositoryImpl): AlertRepository
}
