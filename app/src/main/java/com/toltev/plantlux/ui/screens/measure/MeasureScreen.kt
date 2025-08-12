package com.toltev.plantlux.ui.screens.measure

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Pantalla principal de medición de lux en tiempo real
@Composable
fun MeasureScreen(
    viewModel: MeasureViewModel,
) {
    val lux by viewModel.lux.collectAsState()
    val selectedSpecies by viewModel.selectedSpecies.collectAsState()
    val lightBand by viewModel.lightBand.collectAsState()
    val showSaveDialog = remember { mutableStateOf(false) }
    val showSensorInfoDialog = remember { mutableStateOf(false) }
    val hasSensor by viewModel.hasSensor.collectAsState()

    // Animar el valor de lux para suavizar cambios visuales
    val animatedLux by animateFloatAsState(targetValue = lux ?: 0f, label = "luxAnim")

    Box(modifier = Modifier.fillMaxSize()) {
        if (hasSensor) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lectura de lux en tiempo real animada
                Text(
                    text = if (lux != null) "${"%.0f".format(animatedLux)} lux" else "-",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { contentDescription = "Lectura de lux: ${lux?.let { "${"%.0f".format(it)}" } ?: "-"}" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = viewModel.getAmbienceLabel(lux),
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                SpeciesDropdown(
                    speciesList = viewModel.speciesList.collectAsState().value,
                    selected = selectedSpecies,
                    onSelect = { viewModel.onSpeciesSelected(it) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Transición suave del chip de estado
                Crossfade(targetState = lightBand, label = "bandAnim") { band ->
                    LightBandChip(band = band)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { showSaveDialog.value = true },
                    modifier = Modifier.semantics { contentDescription = "Guardar punto de luz" }
                ) {
                    Text("Guardar punto de luz")
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Botón de debug para mostrar información del sensor
                OutlinedButton(
                    onClick = { showSensorInfoDialog.value = true },
                    modifier = Modifier.semantics { contentDescription = "Información del sensor" }
                ) {
                    Text("Info del Sensor")
                }
            }
            if (showSaveDialog.value) {
                SaveSpotDialog(
                    onSave = { name ->
                        viewModel.saveSpot(name)
                        showSaveDialog.value = false
                    },
                    onDismiss = { showSaveDialog.value = false }
                )
            }
            if (showSensorInfoDialog.value) {
                SensorInfoDialog(
                    sensorInfo = viewModel.sensorInfo,
                    onDismiss = { showSensorInfoDialog.value = false }
                )
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Sin sensor de luz",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No se detectó sensor de luz en este dispositivo.",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "La medición de lux no está disponible.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                )
            }
        }
    }
}

// Composable para el selector de especie (dropdown)
@Composable
fun SpeciesDropdown(
    speciesList: List<String>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    // Implementación básica de dropdown
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(onClick = { expanded = true }) {
            Text(selected ?: "Selecciona especie")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            speciesList.forEach { species ->
                DropdownMenuItem(
                    text = { Text(species) },
                    onClick = {
                        onSelect(species)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Composable para mostrar el chip de estado de luz (Baja/Media/Alta)
@Composable
fun LightBandChip(band: String?) {
    if (band == null) return
    val color = when (band) {
        "BAJA" -> MaterialTheme.colorScheme.primaryContainer
        "MEDIA" -> MaterialTheme.colorScheme.secondaryContainer
        "ALTA" -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(
        color = color,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "Luz $band",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

// Composable para el diálogo de guardar spot
@Composable
fun SaveSpotDialog(onSave: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Guardar punto de luz") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del spot") }
            )
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onSave(name) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Composable para mostrar información del sensor
@Composable
fun SensorInfoDialog(sensorInfo: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Información del Sensor") },
        text = {
            Text(
                text = sensorInfo,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
