package com.toltev.plantlux.domain.usecase

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.toltev.plantlux.data.repo.ReadingRepository
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

// Caso de uso para exportar mediciones a CSV y compartir
class ExportCsvUseCase(
    private val context: Context,
    private val readingRepository: ReadingRepository
) {
    // Exporta mediciones a CSV. Si spotId es null, exporta todas; si no, solo las de ese spot.
    suspend fun export(spotId: Long? = null): File {
        val entities = if (spotId == null) {
            readingRepository.getAllReadings().first()
        } else {
            readingRepository.getReadingsForSpot(spotId).first()
        }
        val readings = entities.map { com.toltev.plantlux.domain.model.Reading(id = it.id, spotId = it.spotId, timestamp = it.timestamp, lux = it.lux) }
        val file = createCsvFile(readings)
        return file
    }

    // Crea el archivo CSV en el almacenamiento interno de la app
    private fun createCsvFile(readings: List<com.toltev.plantlux.domain.model.Reading>): File {
        val sdf = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.getDefault())
        val fileName = "mediciones_${sdf.format(Date())}.csv"
        val file = File(context.filesDir, fileName)
        FileWriter(file).use { writer ->
            writer.appendLine("spotId,timestamp,lux")
            readings.forEach {
                writer.appendLine("${it.spotId},${it.timestamp},${it.lux}")
            }
        }
        return file
    }

    // Prepara un Intent para compartir el archivo CSV
    fun getShareIntent(file: File): Intent {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            file
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}

// Nota: Recuerda declarar el FileProvider en el AndroidManifest.xml y crear el archivo de paths XML.
