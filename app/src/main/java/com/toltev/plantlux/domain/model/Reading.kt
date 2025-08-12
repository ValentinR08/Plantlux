package com.toltev.plantlux.domain.model

// Modelo de dominio para una medición de lux
data class Reading(
    val id: Long = 0,
    val spotId: Long,
    val timestamp: Long,
    val lux: Float
)



