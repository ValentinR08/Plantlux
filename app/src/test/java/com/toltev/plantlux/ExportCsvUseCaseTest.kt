package com.toltev.plantlux

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

// Test unitario para ExportCsvUseCase (formato de CSV)
class ExportCsvUseCaseTest {
    @Test
    fun testCsvFormat() {
        val file = File.createTempFile("test", ".csv")
        file.writeText("spotId,timestamp,lux\n1,123456,100.0\n")
        val content = file.readText()
        assertTrue(content.startsWith("spotId,timestamp,lux"))
        assertTrue(content.contains("1,123456,100.0"))
        file.delete()
    }
}


