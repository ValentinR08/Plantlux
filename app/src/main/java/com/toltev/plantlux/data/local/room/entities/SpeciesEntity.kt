package com.toltev.plantlux.data.local.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad que representa una especie de planta y sus rangos de luz recomendados
@Entity(tableName = "species")
data class SpeciesEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val lowFrom: Int,
    val lowTo: Int,
    val midFrom: Int,
    val midTo: Int,
    val highFrom: Int,
    val highTo: Int,
    val description: String = "",
    val editable: Boolean = false
)

