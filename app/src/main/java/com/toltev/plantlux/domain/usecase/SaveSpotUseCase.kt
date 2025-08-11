package com.toltev.plantlux.domain.usecase

import com.toltev.plantlux.data.repo.SpotRepository
import com.toltev.plantlux.domain.model.Spot
import com.toltev.plantlux.data.local.room.entities.SpotEntity

// Caso de uso para guardar un nuevo punto de luz (spot)
class SaveSpotUseCase(private val spotRepository: SpotRepository) {
    suspend fun execute(spot: Spot): Long {
        val entity = SpotEntity(
            id = spot.id,
            name = spot.name,
            notes = spot.notes,
            createdAt = spot.createdAt
        )
        return spotRepository.insert(entity)
    }
}
