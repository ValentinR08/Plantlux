package com.toltev.plantlux

import com.toltev.plantlux.domain.model.Reading
import com.toltev.plantlux.domain.usecase.ComputeSpotStatsUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

// Test unitario para ComputeSpotStatsUseCase
class ComputeSpotStatsUseCaseTest {
    private val useCase = ComputeSpotStatsUseCase()

    @Test
    fun testHourlyAverages() {
        val readings = listOf(
            Reading(id = 1, spotId = 1, timestamp = 1620000000000, lux = 100f), // 0h
            Reading(id = 2, spotId = 1, timestamp = 1620003600000, lux = 200f), // 1h
            Reading(id = 3, spotId = 1, timestamp = 1620007200000, lux = 300f)  // 2h
        )
        val averages = useCase.computeHourlyAverages(readings)
        assertEquals(100f, averages[0] ?: 0f, 0.01f)
        assertEquals(200f, averages[1] ?: 0f, 0.01f)
        assertEquals(300f, averages[2] ?: 0f, 0.01f)
    }

    @Test
    fun testBestHour() {
        val averages = mapOf(0 to 100f, 1 to 200f, 2 to 300f)
        val best = useCase.bestHour(averages)
        assertEquals(2, best)
    }
}



