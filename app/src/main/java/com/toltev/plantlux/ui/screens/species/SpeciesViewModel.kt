package com.toltev.plantlux.ui.screens.species

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.toltev.plantlux.domain.usecase.GetSpeciesRangesUseCase
import com.toltev.plantlux.data.prefs.SettingsDataStore
import com.toltev.plantlux.domain.model.Species
import com.toltev.plantlux.domain.model.SpeciesOverride
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableStateFlow

// ViewModel para la pantalla de catálogo y edición de especies
@HiltViewModel
class SpeciesViewModel @Inject constructor(
    private val getSpeciesRangesUseCase: GetSpeciesRangesUseCase,
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {
    // Flujos de especies desde Room y overrides desde DataStore
    private val roomSpecies: Flow<List<Species>> = getSpeciesRangesUseCase.execute()
    private val overrides: Flow<Map<String, SpeciesOverride>> = settingsDataStore.getAllSpeciesOverrides()

    // Búsqueda
    private val _query = MutableStateFlow("")
    fun onSearch(q: String) { _query.value = q }

    // Exponer la lista de especies con rangos efectivos (Room + overrides)
    val speciesList: StateFlow<List<Species>> = combine(roomSpecies, overrides, _query) { speciesList, overridesMap, query ->
        speciesList
            .map { species ->
                val override = overridesMap[species.name]
                if (override != null) {
                    species.copy(
                        lowFrom = override.lowFrom ?: species.lowFrom,
                        lowTo = override.lowTo ?: species.lowTo,
                        midFrom = override.midFrom ?: species.midFrom,
                        midTo = override.midTo ?: species.midTo,
                        highFrom = override.highFrom ?: species.highFrom,
                        highTo = override.highTo ?: species.highTo
                    )
                } else species
            }
            .filter { it.name.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

// Eliminado duplicado: se usa com.toltev.plantlux.domain.model.SpeciesOverride
