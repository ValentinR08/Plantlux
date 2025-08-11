package com.toltev.plantlux.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.toltev.plantlux.domain.usecase.ExportCsvUseCase
import com.toltev.plantlux.data.prefs.SettingsDataStore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import java.io.File

// ViewModel para la pantalla de ajustes de la app
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val exportCsvUseCase: ExportCsvUseCase,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    // Flujos de ajustes
    val alphaEMA: StateFlow<Float> = settingsDataStore.alphaEMA.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.25f)
    val alertEnabled: StateFlow<Boolean> = settingsDataStore.alertEnabled.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val alertThreshold: StateFlow<Int> = settingsDataStore.alertThreshold.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    // Overrides simples (placeholder). Se podría derivar desde preferencesFlow si se requiere
    data class Ranges(val lowFrom: Int = 0, val lowTo: Int = 0, val midFrom: Int = 0, val midTo: Int = 0, val highFrom: Int = 0, val highTo: Int = 0)
    val speciesOverrides: StateFlow<Map<String, Ranges>> = settingsDataStore.preferencesFlow
        .map { emptyMap<String, Ranges>() } // Derivación real opcional
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun setAlphaEMA(value: Float) { viewModelScope.launch { settingsDataStore.setAlphaEMA(value) } }
    fun setAlertEnabled(enabled: Boolean) { viewModelScope.launch { settingsDataStore.setAlertEnabled(enabled) } }
    fun setAlertThreshold(value: Int) { viewModelScope.launch { settingsDataStore.setAlertThreshold(value) } }
    fun setSpeciesOverride(species: String, range: String, value: Int) { viewModelScope.launch { settingsDataStore.setSpeciesThresholdOverride(species, range, value) } }

    // Exportación CSV global
    private val _csvFile = MutableSharedFlow<File>(extraBufferCapacity = 1)
    val csvFile: SharedFlow<File> = _csvFile

    fun exportAllCsv() {
        viewModelScope.launch {
            val file = exportCsvUseCase.export(null)
            _csvFile.emit(file)
        }
    }
}
