package com.toltev.plantlux.data.repo

import com.toltev.plantlux.data.local.room.dao.ReadingDao
import com.toltev.plantlux.data.local.room.entities.ReadingEntity
import kotlinx.coroutines.flow.Flow

// Repositorio para acceder a las mediciones de lux
class ReadingRepository(private val readingDao: ReadingDao) {
    fun getReadingsForSpot(spotId: Long): Flow<List<ReadingEntity>> = readingDao.getReadingsForSpot(spotId)
    suspend fun getReadingsForSpotInRange(spotId: Long, from: Long, to: Long): List<ReadingEntity> = readingDao.getReadingsForSpotInRange(spotId, from, to)
    fun getAllReadings(): Flow<List<ReadingEntity>> = readingDao.getAllReadings()
    suspend fun insert(reading: ReadingEntity): Long = readingDao.insert(reading)
}

