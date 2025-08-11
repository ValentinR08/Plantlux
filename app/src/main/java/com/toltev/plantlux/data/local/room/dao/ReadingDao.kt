package com.toltev.plantlux.data.local.room.dao

import androidx.room.*
import com.toltev.plantlux.data.local.room.entities.ReadingEntity
import kotlinx.coroutines.flow.Flow

// DAO para acceder a las mediciones de lux
@Dao
interface ReadingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reading: ReadingEntity): Long

    @Query("SELECT * FROM readings WHERE spotId = :spotId ORDER BY timestamp DESC")
    fun getReadingsForSpot(spotId: Long): Flow<List<ReadingEntity>>

    @Query("SELECT * FROM readings WHERE spotId = :spotId AND timestamp BETWEEN :from AND :to ORDER BY timestamp ASC")
    suspend fun getReadingsForSpotInRange(spotId: Long, from: Long, to: Long): List<ReadingEntity>

    @Query("SELECT * FROM readings")
    fun getAllReadings(): Flow<List<ReadingEntity>>
}

