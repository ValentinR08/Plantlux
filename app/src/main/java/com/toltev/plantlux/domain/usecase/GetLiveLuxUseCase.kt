package com.toltev.plantlux.domain.usecase

import com.toltev.plantlux.sensors.LightSensorManager
import kotlinx.coroutines.flow.StateFlow

// Caso de uso para obtener la lectura de lux en tiempo real
class GetLiveLuxUseCase(private val lightSensorManager: LightSensorManager) {
    fun execute(): StateFlow<Float?> = lightSensorManager.luxFlow
}



