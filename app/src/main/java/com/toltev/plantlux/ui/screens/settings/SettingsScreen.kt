package com.toltev.plantlux.ui.screens.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// Pantalla de ajustes de la app
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel
) {
    val alpha by viewModel.alphaEMA.collectAsState()
    val alertEnabled by viewModel.alertEnabled.collectAsState()
    val alertThreshold by viewModel.alertThreshold.collectAsState()
    val speciesOverrides by viewModel.speciesOverrides.collectAsState()
    val context = LocalContext.current

    // Efecto para compartir el archivo CSV cuando esté listo
    LaunchedEffect(Unit) {
        viewModel.csvFile.collectLatest { file ->
            shareCsv(context, file)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "Ajustes",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Ajuste de alpha EMA
        Text("Suavizado EMA (alpha): ${"%.2f".format(alpha)}", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp))
        Slider(
            value = alpha,
            onValueChange = { viewModel.setAlphaEMA(it) },
            valueRange = 0.1f..0.5f,
            steps = 3,
            modifier = Modifier.semantics { contentDescription = "Ajustar suavizado EMA" }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Ajuste de alertas
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Alertas de luz baja", style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp))
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = alertEnabled,
                onCheckedChange = { viewModel.setAlertEnabled(it) },
                modifier = Modifier.semantics { contentDescription = "Activar o desactivar alertas de luz baja" }
            )
        }
        if (alertEnabled) {
            OutlinedTextField(
                value = alertThreshold.toString(),
                onValueChange = { viewModel.setAlertThreshold(it.toIntOrNull() ?: 0) },
                label = { Text("Umbral de lux para alerta") },
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Umbral de lux para alerta" }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Overrides de rangos por especie
        Text("Personalizar rangos por especie", style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp))
        if (speciesOverrides.isEmpty()) {
            // Estado vacío para overrides
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Sin overrides",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "No hay rangos personalizados.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )
            }
        } else {
            speciesOverrides.forEach { (species, ranges) ->
                Text(species, style = MaterialTheme.typography.labelLarge.copy(fontSize = 15.sp))
                Row {
                    OutlinedTextField(
                        value = ranges.lowFrom.toString(),
                        onValueChange = { viewModel.setSpeciesOverride(species, "lowFrom", it.toIntOrNull() ?: 0) },
                        label = { Text("Baja desde") },
                        modifier = Modifier.weight(1f).semantics { contentDescription = "Baja desde de $species" }
                    )
                    OutlinedTextField(
                        value = ranges.lowTo.toString(),
                        onValueChange = { viewModel.setSpeciesOverride(species, "lowTo", it.toIntOrNull() ?: 0) },
                        label = { Text("Baja hasta") },
                        modifier = Modifier.weight(1f).semantics { contentDescription = "Baja hasta de $species" }
                    )
                }
                Row {
                    OutlinedTextField(
                        value = ranges.midFrom.toString(),
                        onValueChange = { viewModel.setSpeciesOverride(species, "midFrom", it.toIntOrNull() ?: 0) },
                        label = { Text("Media desde") },
                        modifier = Modifier.weight(1f).semantics { contentDescription = "Media desde de $species" }
                    )
                    OutlinedTextField(
                        value = ranges.midTo.toString(),
                        onValueChange = { viewModel.setSpeciesOverride(species, "midTo", it.toIntOrNull() ?: 0) },
                        label = { Text("Media hasta") },
                        modifier = Modifier.weight(1f).semantics { contentDescription = "Media hasta de $species" }
                    )
                }
                Row {
                    OutlinedTextField(
                        value = ranges.highFrom.toString(),
                        onValueChange = { viewModel.setSpeciesOverride(species, "highFrom", it.toIntOrNull() ?: 0) },
                        label = { Text("Alta desde") },
                        modifier = Modifier.weight(1f).semantics { contentDescription = "Alta desde de $species" }
                    )
                    OutlinedTextField(
                        value = ranges.highTo.toString(),
                        onValueChange = { viewModel.setSpeciesOverride(species, "highTo", it.toIntOrNull() ?: 0) },
                        label = { Text("Alta hasta") },
                        modifier = Modifier.weight(1f).semantics { contentDescription = "Alta hasta de $species" }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Botón para exportar CSV
        Button(
            onClick = { viewModel.exportAllCsv() },
            modifier = Modifier.semantics { contentDescription = "Exportar todas las mediciones a CSV" }
        ) {
            Text("Exportar todas las mediciones a CSV")
        }
    }
}

// Compartir archivo CSV usando FileProvider
private fun shareCsv(context: Context, file: File) {
    val uri: Uri = FileProvider.getUriForFile(
        context,
        context.packageName + ".provider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Compartir mediciones CSV"))
}
