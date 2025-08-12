package com.toltev.plantlux.di

import android.content.Context
import com.toltev.plantlux.data.local.room.PlantLuxDb
import com.toltev.plantlux.data.local.room.dao.ReadingDao
import com.toltev.plantlux.data.local.room.dao.SpotDao
import com.toltev.plantlux.data.local.room.dao.SpeciesDao
import com.toltev.plantlux.data.prefs.SettingsDataStore
import com.toltev.plantlux.data.repo.ReadingRepository
import com.toltev.plantlux.data.repo.SpotRepository
import com.toltev.plantlux.data.repo.SpeciesRepository
import com.toltev.plantlux.domain.usecase.*
import com.toltev.plantlux.sensors.LightSensorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Módulo de Hilt para proveer dependencias principales de la app
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // Proveer instancia de Room
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PlantLuxDb =
        PlantLuxDb.getInstance(context)

    @Provides
    fun provideSpotDao(db: PlantLuxDb): SpotDao = db.spotDao()
    @Provides
    fun provideReadingDao(db: PlantLuxDb): ReadingDao = db.readingDao()
    @Provides
    fun provideSpeciesDao(db: PlantLuxDb): SpeciesDao = db.speciesDao()

    // Proveer DataStore para ajustes
    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore =
        SettingsDataStore(context)

    // Proveer repositorios
    @Provides
    @Singleton
    fun provideSpotRepository(spotDao: SpotDao): SpotRepository = SpotRepository(spotDao)
    @Provides
    @Singleton
    fun provideReadingRepository(readingDao: ReadingDao): ReadingRepository = ReadingRepository(readingDao)
    @Provides
    @Singleton
    fun provideSpeciesRepository(speciesDao: SpeciesDao): SpeciesRepository = SpeciesRepository(speciesDao)

    // Proveer casos de uso
    @Provides
    fun provideGetLiveLuxUseCase(lightSensorManager: LightSensorManager): GetLiveLuxUseCase =
        GetLiveLuxUseCase(lightSensorManager)
    @Provides
    fun provideSaveSpotUseCase(spotRepository: SpotRepository): SaveSpotUseCase =
        SaveSpotUseCase(spotRepository)
    @Provides
    fun provideRecordReadingUseCase(readingRepository: ReadingRepository): RecordReadingUseCase =
        RecordReadingUseCase(readingRepository)
    @Provides
    fun provideGetSpeciesRangesUseCase(speciesRepository: SpeciesRepository): GetSpeciesRangesUseCase =
        GetSpeciesRangesUseCase(speciesRepository)
    @Provides
    fun provideComputeSpotStatsUseCase(): ComputeSpotStatsUseCase = ComputeSpotStatsUseCase()
    @Provides
    fun provideExportCsvUseCase(@ApplicationContext context: Context, readingRepository: ReadingRepository): ExportCsvUseCase =
        ExportCsvUseCase(context, readingRepository)

    // Proveer LightSensorManager
    @Provides
    @Singleton
    fun provideLightSensorManager(@ApplicationContext context: Context, settingsDataStore: SettingsDataStore): LightSensorManager =
        LightSensorManager(context) // Puedes leer alpha de settingsDataStore si lo deseas
}



