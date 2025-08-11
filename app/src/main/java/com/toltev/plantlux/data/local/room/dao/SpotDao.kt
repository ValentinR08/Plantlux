package com.toltev.plantlux.data.local.room.dao

import androidx.room.*
import com.toltev.plantlux.data.local.room.entities.SpotEntity
import kotlinx.coroutines.flow.Flow

// DAO para acceder a los puntos de luz (spots)
@Dao
interface SpotDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(spot: SpotEntity): Long

    @Update
    suspend fun update(spot: SpotEntity)

    @Delete
    suspend fun delete(spot: SpotEntity)

    @Query("SELECT * FROM spots ORDER BY createdAt DESC")
    fun getAllSpots(): Flow<List<SpotEntity>>

    @Query("SELECT * FROM spots WHERE id = :id")
    suspend fun getSpotById(id: Long): SpotEntity?
}

