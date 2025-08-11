package com.toltev.plantlux

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Punto de entrada de la aplicación PlantLux
// Se anota con HiltAndroidApp para habilitar inyección de dependencias
@HiltAndroidApp
class PlantLuxApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicialización global si es necesario
    }
}
