package com.toltev.plantlux.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad que representa una medición de lux asociada a un spot
@Entity(tableName = "readings")
data class ReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val spotId: Long,
    val timestamp: Long,
    val lux: Float
)

