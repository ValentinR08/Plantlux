package com.toltev.plantlux.ui.screens.spots

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
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
    val hourlyAverages by viewModel.getHourlyAveragesFlow(spotId).collectAsState()
    val isMeasuring by viewModel.isMeasuring.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val context = LocalContext.current

    // Efecto para compartir el archivo CSV cuando esté listo
    LaunchedEffect(Unit) {
        viewModel.csvFile.collectLatest { file ->
            shareCsv(context = context, file = file)
        }
    }

    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("Detalle del punto de luz", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        // Información del spot
        val spot = viewModel.spots.collectAsState().value.find { it.id == spotId }
        spot?.let { spotInfo ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = spotInfo.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    if (!spotInfo.notes.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = spotInfo.notes,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Creado: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(spotInfo.createdAt))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Estadísticas básicas optimizadas
        val stats by remember(hourlyAverages) {
            derivedStateOf {
                if (hourlyAverages.isNotEmpty()) {
                    val maxLux = hourlyAverages.values.maxOrNull() ?: 0f
                    val minLux = hourlyAverages.values.minOrNull() ?: 0f
                    val avgLux = hourlyAverages.values.average().toFloat()
                    val totalReadings = hourlyAverages.values.count { it > 0 }
                    StatsData(maxLux, minLux, avgLux, totalReadings)
                } else null
            }
        }
        
        if (stats != null) {
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Estadísticas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Máximo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${"%.0f".format(stats!!.maxLux)} lux",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = "Promedio",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${"%.0f".format(stats!!.avgLux)} lux",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = "Mínimo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${"%.0f".format(stats!!.minLux)} lux",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                        Column {
                            Text(
                                text = "Lecturas",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${stats!!.totalReadings}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Gráfico simple de lux vs hora
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Historial de mediciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                LuxChart(hourlyAverages)
            }
        }
        
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

// Clase de datos para las estadísticas
private data class StatsData(
    val maxLux: Float,
    val minLux: Float,
    val avgLux: Float,
    val totalReadings: Int
)



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
    val chartHeight = 120.dp
    val chartWidth = 320.dp
    
    if (hourlyAverages.isEmpty()) {
        // Mostrar mensaje cuando no hay datos
        Box(
            modifier = Modifier.size(chartWidth, chartHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No hay datos de medición",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Lux vs hora", style = MaterialTheme.typography.bodySmall)
        return
    }
    
    // Memoizar los cálculos para evitar recálculos innecesarios
    val chartData by remember(hourlyAverages) {
        derivedStateOf {
            val maxLux = hourlyAverages.values.maxOrNull() ?: 1f
            val minLux = hourlyAverages.values.minOrNull() ?: 0f
            val luxRange = maxLux - minLux
            val effectiveMaxLux = if (luxRange > 0) maxLux else maxLux + 1f
            Triple(maxLux, effectiveMaxLux, hourlyAverages)
        }
    }
    
    val (maxLux, effectiveMaxLux, data) = chartData
    val hours = (0..23).toList()
    
    Box(modifier = Modifier.size(chartWidth, chartHeight)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val hourStep = width / 23f
            
            // Dibujar líneas de fondo para las horas
            for (i in 0..23 step 6) {
                val x = i * hourStep
                drawLine(
                    color = Color.Gray.copy(alpha = 0.3f),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1f
                )
            }
            
            // Dibujar el gráfico de líneas
            var prevX = 0f
            var prevY = height
            var hasDrawnFirstPoint = false
            
            hours.forEachIndexed { i, hour ->
                val lux = data[hour] ?: 0f
                val x = i * hourStep
                val y = if (effectiveMaxLux > 0) {
                    height - (lux / effectiveMaxLux * height)
                } else {
                    height / 2f
                }
                
                if (hasDrawnFirstPoint && lux > 0) {
                    drawLine(
                        color = Color(0xFF4CAF50),
                        start = Offset(prevX, prevY),
                        end = Offset(x, y),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }
                
                // Dibujar punto si hay datos
                if (lux > 0) {
                    drawCircle(
                        color = Color(0xFF4CAF50),
                        radius = 4f,
                        center = Offset(x, y)
                    )
                    hasDrawnFirstPoint = true
                }
                
                prevX = x
                prevY = y
            }
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        "Lux vs hora (Max: ${"%.0f".format(maxLux)} lux)", 
        style = MaterialTheme.typography.bodySmall
    )
}
