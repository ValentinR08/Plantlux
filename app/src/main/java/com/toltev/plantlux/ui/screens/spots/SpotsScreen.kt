package com.toltev.plantlux.ui.screens.spots

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.toltev.plantlux.R

// Pantalla de lista de spots guardados
@Composable
fun SpotsScreen(
    viewModel: SpotsViewModel,
    onSpotClick: (Long) -> Unit
) {
    val spots by viewModel.spots.collectAsState()
    val stats by viewModel.spotStats.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Puntos de luz guardados",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Crossfade(targetState = spots.isEmpty(), label = "spotsAnim") { isEmpty ->
            if (isEmpty) {
                // Estado vacío con ilustración y mensaje
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddCircleOutline,
                            contentDescription = "Sin spots",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Aún no has guardado ningún punto de luz.",
                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Toca el botón de medición para guardar el primer spot.",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            } else {
                LazyColumn {
                    items(spots) { spot ->
                        val stat = stats[spot.id]
                        SpotItem(
                            name = spot.name,
                            avgLux = stat?.avgLux,
                            bestHour = stat?.bestHour,
                            onClick = { onSpotClick(spot.id) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

// Composable para mostrar un spot en la lista
@Composable
fun SpotItem(name: String, avgLux: Float?, bestHour: Int?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
            .semantics { contentDescription = "Spot: $name" },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            )
            avgLux?.let {
                Text(
                    "Lux promedio: ${"%.0f".format(it)}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
                )
            }
            bestHour?.let {
                Text(
                    "Mejor hora: $it h",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 14.sp)
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ver detalle de $name",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(28.dp)
        )
    }
}
