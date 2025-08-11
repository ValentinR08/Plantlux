package com.toltev.plantlux.data.local.room.dao

import androidx.room.*
import com.toltev.plantlux.data.local.room.entities.SpeciesEntity
import kotlinx.coroutines.flow.Flow

// DAO para acceder a las especies de plantas
@Dao
interface SpeciesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(species: List<SpeciesEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(species: SpeciesEntity): Long

    @Update
    suspend fun update(species: SpeciesEntity)

    @Query("SELECT * FROM species ORDER BY name ASC")
    fun getAllSpecies(): Flow<List<SpeciesEntity>>

    @Query("SELECT * FROM species WHERE id = :id")
    suspend fun getSpeciesById(id: Long): SpeciesEntity?

    @Query("SELECT * FROM species WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchSpecies(query: String): Flow<List<SpeciesEntity>>
}

