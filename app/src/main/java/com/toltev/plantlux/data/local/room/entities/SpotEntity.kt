package com.toltev.plantlux.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad que representa un punto de luz (spot) guardado por el usuario
@Entity(tableName = "spots")
data class SpotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

