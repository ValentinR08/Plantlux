package com.toltev.plantlux.domain.usecase

import com.toltev.plantlux.domain.model.Reading
import java.util.Calendar
import kotlin.math.roundToInt

// Caso de uso para calcular estadísticas de un spot (promedio por hora, mejor franja)
class ComputeSpotStatsUseCase {
    // Devuelve un mapa de hora (0-23) a promedio de lux
    fun computeHourlyAverages(readings: List<Reading>): Map<Int, Float> {
        if (readings.isEmpty()) return emptyMap()
        val buckets = HashMap<Int, MutableList<Float>>()
        val calendar = Calendar.getInstance()
        readings.forEach { r ->
            calendar.timeInMillis = r.timestamp
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            buckets.getOrPut(hour) { mutableListOf() }.add(r.lux)
        }
        return buckets.mapValues { (_, values) ->
            (values.sum() / values.size)
        }
    }

    // Devuelve la mejor hora (mayor promedio de lux)
    fun bestHour(hourlyAverages: Map<Int, Float>): Int? =
        hourlyAverages.maxByOrNull { it.value }?.key
}
