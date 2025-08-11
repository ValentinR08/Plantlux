package com.toltev.plantlux.domain.model

// Modelo de dominio para una especie de planta y sus rangos de luz
data class Species(
    val id: Long = 0,
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

