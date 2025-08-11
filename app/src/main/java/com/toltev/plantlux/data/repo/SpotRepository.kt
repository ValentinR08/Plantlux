package com.toltev.plantlux.data.repo

import com.toltev.plantlux.data.local.room.dao.SpotDao
import com.toltev.plantlux.data.local.room.entities.SpotEntity
import kotlinx.coroutines.flow.Flow

// Repositorio para acceder a los puntos de luz (spots)
class SpotRepository(private val spotDao: SpotDao) {
    fun getAllSpots(): Flow<List<SpotEntity>> = spotDao.getAllSpots()
    suspend fun getSpotById(id: Long): SpotEntity? = spotDao.getSpotById(id)
    suspend fun insert(spot: SpotEntity): Long = spotDao.insert(spot)
    suspend fun update(spot: SpotEntity) = spotDao.update(spot)
    suspend fun delete(spot: SpotEntity) = spotDao.delete(spot)
}

