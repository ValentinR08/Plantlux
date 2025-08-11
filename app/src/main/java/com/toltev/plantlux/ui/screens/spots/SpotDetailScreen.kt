package com.toltev.plantlux.ui.screens.spots

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import kotlinx.coroutines.flow.collectLatest
import java.io.File

// Pantalla de detalle de un spot
@Composable
fun SpotDetailScreen(
    spotId: Long,
    viewModel: SpotsViewModel,
    onBack: () -> Unit
) {
    val readings = viewModel.getReadingsForSpot(spotId)
    val hourlyAverages = viewModel.getHourlyAverages(spotId)
    val isMeasuring by viewModel.isMeasuring.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val context = LocalContext.current

    // Efecto para compartir el archivo CSV cuando esté listo
    LaunchedEffect(Unit) {
        viewModel.csvFile.collectLatest { file ->
            shareCsv(context = context, file = file)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Detalle del punto de luz", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        // Gráfico simple de lux vs hora
        LuxChart(hourlyAverages)
        Spacer(modifier = Modifier.height(24.dp))
        if (isMeasuring) {
            // Feedback visual durante la medición
            Text("Midiendo...", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(onClick = { viewModel.cancelMeasurement() }) {
                Text("Cancelar medición")
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { viewModel.measureOneMinute(spotId) }) {
                    Text("Medir 1 min")
                }
                OutlinedButton(onClick = { viewModel.exportCsv(spotId) }) {
                    Text("Exportar CSV")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

private fun shareCsv(context: android.content.Context, file: File) {
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

// Gráfico simple de lux vs hora usando Canvas
@Composable
fun LuxChart(hourlyAverages: Map<Int, Float>) {
    val maxLux = hourlyAverages.values.maxOrNull() ?: 1f
    val hours = (0..23).toList()
    val chartHeight = 120.dp
    val chartWidth = 320.dp
    Box(modifier = Modifier.size(chartWidth, chartHeight)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val hourStep = width / 23f
            var prevX = 0f
            var prevY = height
            hours.forEachIndexed { i, hour ->
                val lux = hourlyAverages[hour] ?: 0f
                val x = i * hourStep
                val y = height - (lux / maxLux * height)
                if (i > 0) {
                    drawLine(
                        color = Color(0xFF4CAF50),
                        start = Offset(prevX, prevY),
                        end = Offset(x, y),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
                prevX = x
                prevY = y
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text("Lux vs hora", style = MaterialTheme.typography.bodySmall)
}
