package com.toltev.plantlux.data.repo

import com.toltev.plantlux.data.local.room.dao.SpeciesDao
import com.toltev.plantlux.data.local.room.entities.SpeciesEntity
import kotlinx.coroutines.flow.Flow

// Repositorio para acceder a las especies de plantas
class SpeciesRepository(private val speciesDao: SpeciesDao) {
    fun getAllSpecies(): Flow<List<SpeciesEntity>> = speciesDao.getAllSpecies()
    suspend fun getSpeciesById(id: Long): SpeciesEntity? = speciesDao.getSpeciesById(id)
    fun searchSpecies(query: String): Flow<List<SpeciesEntity>> = speciesDao.searchSpecies(query)
    suspend fun insert(species: SpeciesEntity): Long = speciesDao.insert(species)
    suspend fun update(species: SpeciesEntity) = speciesDao.update(species)
}



