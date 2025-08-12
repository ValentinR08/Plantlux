package com.toltev.plantlux.ui.screens.spots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.toltev.plantlux.domain.usecase.SaveSpotUseCase
import com.toltev.plantlux.domain.usecase.RecordReadingUseCase
import com.toltev.plantlux.domain.usecase.ComputeSpotStatsUseCase
import com.toltev.plantlux.domain.usecase.GetLiveLuxUseCase
import com.toltev.plantlux.domain.usecase.ExportCsvUseCase
import com.toltev.plantlux.data.repo.SpotRepository
import com.toltev.plantlux.data.repo.ReadingRepository
import com.toltev.plantlux.domain.model.Reading
import com.toltev.plantlux.domain.model.Spot
import com.toltev.plantlux.data.local.room.entities.SpotEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first

// ViewModel para la pantalla de lista y detalle de spots
@HiltViewModel
class SpotsViewModel @Inject constructor(
    private val saveSpotUseCase: SaveSpotUseCase,
    private val recordReadingUseCase: RecordReadingUseCase,
    private val computeSpotStatsUseCase: ComputeSpotStatsUseCase,
    private val getLiveLuxUseCase: GetLiveLuxUseCase,
    private val exportCsvUseCase: ExportCsvUseCase,
    private val spotRepository: SpotRepository,
    private val readingRepository: ReadingRepository
) : ViewModel() {
    // Mapeo simple entidad -> dominio
    private fun SpotEntity.toDomain(): Spot = Spot(id = id, name = name, notes = notes, createdAt = createdAt)

    // Spots como flujo de dominio
    val spots: StateFlow<List<Spot>> = spotRepository.getAllSpots()
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    data class SpotStat(val avgLux: Float, val bestHour: Int?)

    // Estadísticas por spot combinando todas las lecturas
    val spotStats: StateFlow<Map<Long, SpotStat>> = readingRepository.getAllReadings()
        .map { readings ->
            val grouped = readings.groupBy { it.spotId }
            grouped.mapValues { (_, list) ->
                val domainList = list.map { Reading(id = it.id, spotId = it.spotId, timestamp = it.timestamp, lux = it.lux) }
                val hourly = computeSpotStatsUseCase.computeHourlyAverages(domainList)
                val best = computeSpotStatsUseCase.bestHour(hourly)
                val avg = if (domainList.isNotEmpty()) domainList.map { it.lux }.average().toFloat() else 0f
                SpotStat(avgLux = avg, bestHour = best)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Obtener lecturas de un spot (flujo)
    fun getReadingsForSpot(spotId: Long): Flow<List<Reading>> =
        readingRepository.getReadingsForSpot(spotId).map { list ->
            list.map { Reading(id = it.id, spotId = it.spotId, timestamp = it.timestamp, lux = it.lux) }
        }

    // Calcular promedios por hora para un spot (StateFlow)
    fun getHourlyAveragesFlow(spotId: Long): StateFlow<Map<Int, Float>> =
        readingRepository.getReadingsForSpot(spotId)
            .map { readings ->
                val domainReadings = readings.map { 
                    Reading(id = it.id, spotId = it.spotId, timestamp = it.timestamp, lux = it.lux) 
                }
                computeSpotStatsUseCase.computeHourlyAverages(domainReadings)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Estado para feedback visual de la medición
    private val _isMeasuring = MutableStateFlow(false)
    val isMeasuring: StateFlow<Boolean> = _isMeasuring

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private var measureJob: Job? = null

    fun measureOneMinute(spotId: Long) {
        if (_isMeasuring.value) return
        measureJob?.cancel()
        measureJob = viewModelScope.launch {
            _isMeasuring.value = true
            _progress.value = 0f
            val samples = mutableListOf<Float>()
            val totalSamples = 30
            repeat(totalSamples) { i ->
                val lux = getLiveLuxUseCase.execute().value
                lux?.let { samples.add(it) }
                _progress.value = (i + 1) / totalSamples.toFloat()
                delay(2000)
            }
            _isMeasuring.value = false
            _progress.value = 0f
            if (samples.isNotEmpty()) {
                val avg = samples.average().toFloat()
                recordReadingUseCase.execute(
                    Reading(spotId = spotId, timestamp = System.currentTimeMillis(), lux = avg)
                )
            }
        }
    }

    fun cancelMeasurement() {
        measureJob?.cancel()
        _isMeasuring.value = false
        _progress.value = 0f
    }

    private val _csvFile = MutableSharedFlow<File>(extraBufferCapacity = 1)
    val csvFile: SharedFlow<File> = _csvFile

    fun exportCsv(spotId: Long) {
        viewModelScope.launch {
            val file = exportCsvUseCase.export(spotId)
            _csvFile.emit(file)
        }
    }
}
