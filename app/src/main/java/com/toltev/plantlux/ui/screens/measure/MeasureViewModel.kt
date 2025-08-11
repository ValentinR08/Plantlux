package com.toltev.plantlux.ui.screens.measure

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.toltev.plantlux.domain.usecase.GetLiveLuxUseCase
import com.toltev.plantlux.domain.usecase.SaveSpotUseCase
import com.toltev.plantlux.domain.usecase.GetSpeciesRangesUseCase
import com.toltev.plantlux.domain.usecase.RecordReadingUseCase
import com.toltev.plantlux.data.prefs.SettingsDataStore
import com.toltev.plantlux.domain.model.Spot
import com.toltev.plantlux.domain.model.Reading
import com.toltev.plantlux.domain.model.Species
import com.toltev.plantlux.sensors.LightSensorManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ViewModel para la pantalla de medición de lux en tiempo real
@HiltViewModel
class MeasureViewModel @Inject constructor(
    app: Application,
    private val getLiveLuxUseCase: GetLiveLuxUseCase,
    private val saveSpotUseCase: SaveSpotUseCase,
    private val recordReadingUseCase: RecordReadingUseCase,
    private val getSpeciesRangesUseCase: GetSpeciesRangesUseCase,
    private val settingsDataStore: SettingsDataStore,
    private val lightSensorManager: LightSensorManager
) : AndroidViewModel(app) {
    // Exponer el flujo de lux suavizado
    val lux: StateFlow<Float?> = getLiveLuxUseCase.execute()

    // Disponibilidad del sensor
    val hasSensor: StateFlow<Boolean> = lightSensorManager.hasSensor

    // Flujos de ajustes de alerta
    val alertEnabled = settingsDataStore.alertEnabled
    val alertThreshold = settingsDataStore.alertThreshold

    private var hasNotifiedLow = false

    // Flujos de especies y overrides
    private val roomSpecies: Flow<List<Species>> = getSpeciesRangesUseCase.execute()
    private val _selectedSpecies = MutableStateFlow<String?>(null)
    val selectedSpecies: StateFlow<String?> = _selectedSpecies
    val speciesList: StateFlow<List<String>> = roomSpecies.map { it.map(Species::name) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Especie efectiva (por simplicidad, sin overrides aquí; se aplican en SpeciesVM)
    val selectedSpeciesEffective: StateFlow<Species?> = combine(roomSpecies, selectedSpecies) { list, name ->
        list.find { it.name == name }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Clasificación de luz
    val lightBand: StateFlow<String?> = combine(lux, selectedSpeciesEffective) { luxValue, species ->
        if (luxValue == null || species == null) return@combine null
        when (luxValue) {
            in species.lowFrom.toFloat()..species.lowTo.toFloat() -> "BAJA"
            in species.midFrom.toFloat()..species.midTo.toFloat() -> "MEDIA"
            in species.highFrom.toFloat()..species.highTo.toFloat() -> "ALTA"
            else -> if (luxValue < species.lowFrom) "BAJA" else "ALTA"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Alertas de luz baja
        viewModelScope.launch {
            combine(lux, alertEnabled, alertThreshold) { luxValue, enabled, threshold ->
                Triple(luxValue, enabled, threshold)
            }.collect { (luxValue, enabled, threshold) ->
                if (enabled && luxValue != null) {
                    if (luxValue < threshold && !hasNotifiedLow) {
                        sendLowLightNotification()
                        hasNotifiedLow = true
                    } else if (luxValue >= threshold) {
                        hasNotifiedLow = false
                    }
                }
            }
        }
    }

    fun onSpeciesSelected(name: String) { _selectedSpecies.value = name }

    // Etiquetas de ambiente orientativas para el usuario
    fun getAmbienceLabel(lux: Float?): String = when (lux ?: 0f) {
        in 0f..50f -> "Muy oscuro"
        in 50f..150f -> "Interior tenue"
        in 150f..500f -> "Oficina"
        in 500f..10000f -> "Exterior sombra"
        else -> "Sol directo"
    }

    // Guardar un punto de luz con la lectura actual
    fun saveSpot(name: String) {
        viewModelScope.launch {
            val spotId = saveSpotUseCase.execute(Spot(name = name))
            val currentLux = lux.value
            if (currentLux != null) {
                recordReadingUseCase.execute(
                    Reading(spotId = spotId, timestamp = System.currentTimeMillis(), lux = currentLux)
                )
            }
        }
    }

    private fun sendLowLightNotification() {
        val context = getApplication<Application>()
        val channelId = "low_light_alerts"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de luz baja",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("¡Luz insuficiente!")
            .setContentText("La luz es demasiado baja para tus plantas. Toca para ver detalles.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(1001, notification)
    }
}
