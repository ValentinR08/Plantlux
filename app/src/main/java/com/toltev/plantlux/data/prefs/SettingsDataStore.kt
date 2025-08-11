package com.toltev.plantlux.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.toltev.plantlux.domain.model.SpeciesOverride

private const val DATASTORE_NAME = "settings_prefs"
val Context.settingsDataStore by preferencesDataStore(name = DATASTORE_NAME)

class SettingsDataStore(private val context: Context) {
    companion object {
        val ALPHA_EMA_KEY = floatPreferencesKey("alpha_ema")
        val ALERT_ENABLED_KEY = booleanPreferencesKey("alert_enabled")
        val ALERT_THRESHOLD_KEY = intPreferencesKey("alert_threshold")
        const val OVERRIDE_PREFIX = "override_" // override_<species>_<range>
    }

    val alphaEMA: Flow<Float> = context.settingsDataStore.data.map { it[ALPHA_EMA_KEY] ?: 0.25f }
    suspend fun setAlphaEMA(value: Float) { context.settingsDataStore.edit { it[ALPHA_EMA_KEY] = value } }

    val alertEnabled: Flow<Boolean> = context.settingsDataStore.data.map { it[ALERT_ENABLED_KEY] ?: false }
    suspend fun setAlertEnabled(enabled: Boolean) { context.settingsDataStore.edit { it[ALERT_ENABLED_KEY] = enabled } }

    val alertThreshold: Flow<Int> = context.settingsDataStore.data.map { it[ALERT_THRESHOLD_KEY] ?: 100 }
    suspend fun setAlertThreshold(value: Int) { context.settingsDataStore.edit { it[ALERT_THRESHOLD_KEY] = value } }

    suspend fun setSpeciesThresholdOverride(species: String, range: String, value: Int) {
        val key = intPreferencesKey(OVERRIDE_PREFIX + species + "_" + range)
        context.settingsDataStore.edit { prefs -> prefs[key] = value }
    }

    // Flujo crudo de todas las preferencias, útil para derivar overrides combinando con la lista de especies
    val preferencesFlow = context.settingsDataStore.data

    // Devuelve todos los overrides agrupados por especie y mapeados a SpeciesOverride
    fun getAllSpeciesOverrides(): Flow<Map<String, SpeciesOverride>> =
        preferencesFlow.map { prefs ->
            val map = mutableMapOf<String, SpeciesOverride>()
            prefs.asMap().forEach { (key, value) ->
                val name = (key as? Preferences.Key<*>)?.name ?: return@forEach
                if (name.startsWith(OVERRIDE_PREFIX)) {
                    // Formato esperado: override_<Species>_<range>
                    val parts = name.removePrefix(OVERRIDE_PREFIX).split("_")
                    if (parts.size >= 2) {
                        val species = parts.dropLast(1).joinToString("_") // por si el nombre tiene guiones bajos
                        val range = parts.last()
                        val intValue = (value as? Int) ?: return@forEach
                        val current = map[species] ?: SpeciesOverride()
                        val updated = when (range) {
                            "lowFrom" -> current.copy(lowFrom = intValue)
                            "lowTo" -> current.copy(lowTo = intValue)
                            "midFrom" -> current.copy(midFrom = intValue)
                            "midTo" -> current.copy(midTo = intValue)
                            "highFrom" -> current.copy(highFrom = intValue)
                            "highTo" -> current.copy(highTo = intValue)
                            else -> current
                        }
                        map[species] = updated
                    }
                }
            }
            map
        }
}
