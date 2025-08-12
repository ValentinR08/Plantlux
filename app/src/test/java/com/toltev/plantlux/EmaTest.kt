package com.toltev.plantlux

import org.junit.Assert.assertEquals
import org.junit.Test

// Test unitario para la función de suavizado EMA
class EmaTest {
    // Función EMA (igual que en LightSensorManager)
    private fun ema(prev: Float, new: Float, alpha: Float): Float =
        alpha * new + (1 - alpha) * prev

    @Test
    fun testEma() {
        val alpha = 0.25f
        val prev = 100f
        val new = 200f
        val expected = 0.25f * 200f + 0.75f * 100f // 125.0
        val result = ema(prev, new, alpha)
        assertEquals(expected, result, 0.001f)
    }
}



