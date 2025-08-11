package com.toltev.plantlux.domain.usecase

import com.toltev.plantlux.data.repo.ReadingRepository
import com.toltev.plantlux.domain.model.Reading
import com.toltev.plantlux.data.local.room.entities.ReadingEntity

// Caso de uso para registrar una nueva medición de lux
class RecordReadingUseCase(private val readingRepository: ReadingRepository) {
    suspend fun execute(reading: Reading): Long {
        val entity = ReadingEntity(
            id = reading.id,
            spotId = reading.spotId,
            timestamp = reading.timestamp,
            lux = reading.lux
        )
        return readingRepository.insert(entity)
    }
}
