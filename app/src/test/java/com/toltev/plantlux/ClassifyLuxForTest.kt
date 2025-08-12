package com.toltev.plantlux

import com.toltev.plantlux.domain.model.Species
import org.junit.Assert.assertEquals
import org.junit.Test

// Enum para bandas de luz
enum class LightBand { LOW, MEDIUM, HIGH }

// Función utilitaria para clasificar lux según especie
fun classifyLuxFor(species: Species, lux: Float): LightBand = when {
    lux < species.lowFrom -> LightBand.LOW
    lux in species.lowFrom..species.lowTo -> LightBand.LOW
    lux in species.midFrom..species.midTo -> LightBand.MEDIUM
    lux in species.highFrom..species.highTo -> LightBand.HIGH
    lux > species.highTo -> LightBand.HIGH
    else -> LightBand.LOW
}

// Test unitario para classifyLuxFor
class ClassifyLuxForTest {
    private val sansevieria = Species(
        id = 1,
        name = "Sansevieria",
        lowFrom = 50, lowTo = 150,
        midFrom = 150, midTo = 400,
        highFrom = 400, highTo = 800
    )

    @Test
    fun testLow() {
        assertEquals(LightBand.LOW, classifyLuxFor(sansevieria, 100f))
    }
    @Test
    fun testMedium() {
        assertEquals(LightBand.MEDIUM, classifyLuxFor(sansevieria, 200f))
    }
    @Test
    fun testHigh() {
        assertEquals(LightBand.HIGH, classifyLuxFor(sansevieria, 600f))
    }
}



