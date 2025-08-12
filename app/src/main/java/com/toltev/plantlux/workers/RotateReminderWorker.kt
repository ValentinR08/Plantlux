package com.toltev.plantlux.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.toltev.plantlux.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

// Worker para notificar al usuario que debe rotar la maceta de un spot
class RotateReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val spotName = inputData.getString(KEY_SPOT_NAME) ?: "Punto de luz"
        val spotId = inputData.getLong(KEY_SPOT_ID, -1)
        showNotification(spotName, spotId)
        Result.success()
    }

    // Mostrar notificación para rotar la maceta
    private fun showNotification(spotName: String, spotId: Long) {
        val channelId = "rotate_reminder_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Recordatorios de rotación",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
        // Intent para abrir la app y marcar la rotación como hecha
        val intent = Intent(context, /* MainActivity::class.java */ Class.forName("com.toltev.plantlux.MainActivity"))
        intent.putExtra("spotId", spotId)
        intent.action = ACTION_MARK_ROTATED
        val pendingIntent = PendingIntent.getActivity(
            context, spotId.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Rotar maceta: $spotName")
            .setContentText("Toca para registrar que giraste la planta 90°")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(spotId.toInt(), notification)
    }

    companion object {
        const val KEY_SPOT_ID = "spot_id"
        const val KEY_SPOT_NAME = "spot_name"
        const val KEY_DAYS = "days"
        const val ACTION_MARK_ROTATED = "com.toltev.plantlux.ACTION_MARK_ROTATED"

        // Programar el recordatorio periódico para un spot
        fun schedule(context: Context, spotId: Long, spotName: String, days: Long) {
            val workRequest = PeriodicWorkRequestBuilder<RotateReminderWorker>(days, TimeUnit.DAYS)
                .setInputData(androidx.work.Data.Builder()
                    .putLong(KEY_SPOT_ID, spotId)
                    .putString(KEY_SPOT_NAME, spotName)
                    .putLong(KEY_DAYS, days)
                    .build())
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "rotate_reminder_$spotId",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        // Cancelar el recordatorio
        fun cancel(context: Context, spotId: Long) {
            WorkManager.getInstance(context).cancelUniqueWork("rotate_reminder_$spotId")
        }
    }
}

// Función para marcar la rotación como hecha (actualiza timestamp en Spot)
suspend fun markSpotRotated(spotId: Long, spotRepository: com.toltev.plantlux.data.repo.SpotRepository) {
    val spot = spotRepository.getSpotById(spotId)
    if (spot != null) {
        val updated = spot.copy(notes = "Rotado: ${System.currentTimeMillis()}") // Puedes usar un campo específico si lo prefieres
        spotRepository.update(updated)
    }
}



