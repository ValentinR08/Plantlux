package com.toltev.plantlux.domain.model

// Modelo de dominio para un punto de luz (spot)
data class Spot(
    val id: Long = 0,
    val name: String,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

