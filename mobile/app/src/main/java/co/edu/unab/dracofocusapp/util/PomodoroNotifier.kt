package co.edu.unab.dracofocusapp.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import co.edu.unab.dracofocusapp.R
import co.edu.unab.dracofocusapp.data.local.datastore.SettingsDataStore
import kotlinx.coroutines.flow.first

object PomodoroNotifier {
    private const val CHANNEL_ID_SOUND = "pomodoro_channel_sound"
    private const val CHANNEL_ID_SILENT = "pomodoro_channel_silent"
    private const val NOTIFICATION_ID = 1001

    private fun ensureChannels(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (manager.getNotificationChannel(CHANNEL_ID_SOUND) == null) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            val channel = NotificationChannel(CHANNEL_ID_SOUND, "Dracomodoro", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Avisos de cambio de fase del Dracomodoro, con sonido"
                setSound(soundUri, audioAttributes)
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        if (manager.getNotificationChannel(CHANNEL_ID_SILENT) == null) {
            val channel = NotificationChannel(CHANNEL_ID_SILENT, "Dracomodoro (silencioso)", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Avisos de cambio de fase del Dracomodoro, sin sonido"
                setSound(null, null)
                enableVibration(false)
            }
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Siempre muestra la notificación de cambio de fase. La preferencia del
     * usuario solo decide si suena/vibra o no (canal con sonido vs. silencioso).
     */
    suspend fun notifyPhaseFinished(context: Context, title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val soundEnabled = SettingsDataStore(context).notificationSoundEnabled.first()
        ensureChannels(context)
        val channelId = if (soundEnabled) CHANNEL_ID_SOUND else CHANNEL_ID_SILENT

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_timer)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(if (soundEnabled) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Permiso revocado justo antes de notificar; ignoramos silenciosamente.
        }
    }
}
