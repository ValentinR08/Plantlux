package com.toltev.plantlux

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.toltev.plantlux.ui.navigation.AppNav
import com.toltev.plantlux.ui.theme.PlantLuxTheme
import dagger.hilt.android.AndroidEntryPoint

// Actividad principal que hospeda la UI Compose
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlantLuxTheme {
                AppNav()
            }
        }
    }
}

