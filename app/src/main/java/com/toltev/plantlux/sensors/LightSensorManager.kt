package com.toltev.plantlux.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Gestor del sensor de luz ambiental (lux) con suavizado EMA
class LightSensorManager(
    private val context: Context,
    private val alpha: Float = 0.25f // Valor por defecto, configurable
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    init {
        Log.d("LightSensorManager", "Inicializando LightSensorManager")
        if (lightSensor != null) {
            Log.i("LightSensorManager", "Sensor de luz detectado: ${lightSensor.name}")
            Log.d("LightSensorManager", getSensorInfo())
        } else {
            Log.w("LightSensorManager", "No se detectó sensor de luz en el dispositivo")
        }
    }

    // Disponibilidad del sensor
    private val _hasSensor = MutableStateFlow(lightSensor != null)
    val hasSensor: StateFlow<Boolean> = _hasSensor

    // StateFlow que expone el valor suavizado de lux (null si no hay sensor)
    private val _luxFlow = MutableStateFlow<Float?>(null)
    val luxFlow: StateFlow<Float?> = _luxFlow

    // Último valor suavizado
    private var emaValue: Float? = null

    // Indica si el sensor está registrado
    private var isRegistered = false

    fun hasLightSensor(): Boolean = lightSensor != null

    // Método para obtener información de debug del sensor
    fun getSensorInfo(): String {
        return if (lightSensor != null) {
            "Sensor: ${lightSensor.name}, Vendor: ${lightSensor.vendor}, Version: ${lightSensor.version}, " +
            "Max Range: ${lightSensor.maximumRange}, Resolution: ${lightSensor.resolution}, " +
            "Power: ${lightSensor.power}mA, IsRegistered: $isRegistered"
        } else {
            "No hay sensor de luz disponible"
        }
    }

    // Inicia la escucha del sensor
    fun start() {
        if (lightSensor == null) {
            Log.w("LightSensorManager", "No se encontró sensor de luz en el dispositivo")
            _luxFlow.value = null // Fallback: no hay sensor
            _hasSensor.value = false
            return
        }
        if (!isRegistered) {
            try {
                sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
                isRegistered = true
                _hasSensor.value = true
                Log.d("LightSensorManager", "Sensor de luz iniciado correctamente")
            } catch (e: Exception) {
                Log.e("LightSensorManager", "Error al iniciar sensor de luz: ${e.message}")
                _hasSensor.value = false
            }
        }
    }

    // Detiene la escucha del sensor
    fun stop() {
        if (isRegistered) {
            try {
                sensorManager.unregisterListener(this)
                isRegistered = false
                Log.d("LightSensorManager", "Sensor de luz detenido")
            } catch (e: Exception) {
                Log.e("LightSensorManager", "Error al detener sensor de luz: ${e.message}")
            }
        }
    }

    // Callback de nueva lectura del sensor
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val lux = it.values.firstOrNull() ?: return
            emaValue = if (emaValue == null) lux else ema(emaValue!!, lux, alpha)
            _luxFlow.value = emaValue
            Log.v("LightSensorManager", "Nueva lectura de luz: ${"%.1f".format(lux)} lux (suavizado: ${"%.1f".format(emaValue)} lux)")
        }
    }

    // Callback de cambio de precisión (no usado)
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // Función de suavizado EMA (Exponential Moving Average)
    private fun ema(prev: Float, new: Float, alpha: Float): Float =
        alpha * new + (1 - alpha) * prev

    // Utilizar en Compose con DisposableEffect para manejar ciclo de vida
    // Ejemplo:
    // DisposableEffect(Unit) {
    //     lightSensorManager.start()
    //     onDispose { lightSensorManager.stop() }
    // }
}
