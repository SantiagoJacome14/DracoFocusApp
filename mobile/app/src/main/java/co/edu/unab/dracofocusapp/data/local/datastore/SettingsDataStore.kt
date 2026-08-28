package co.edu.unab.dracofocusapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

/**
 * Preferencias generales de la app, persistidas con DataStore para que
 * sobrevivan a cerrar la app.
 */
class SettingsDataStore(private val context: Context) {

    companion object {
        private val NOTIFICATION_SOUND_ENABLED = booleanPreferencesKey("notification_sound_enabled")
        private val SOUND_EFFECTS_ENABLED = booleanPreferencesKey("sound_effects_enabled")
    }

    /**
     * Controla solo el sonido/vibración de las notificaciones del Dracomodoro.
     * La notificación siempre se muestra; esto solo decide si suena o no.
     */
    val notificationSoundEnabled: Flow<Boolean> =
        context.settingsDataStore.data.map { it[NOTIFICATION_SOUND_ENABLED] ?: true }

    suspend fun setNotificationSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[NOTIFICATION_SOUND_ENABLED] = enabled }
    }

    /** Controla los efectos de sonido de interacción (respuestas, lección completada, etc). */
    val soundEffectsEnabled: Flow<Boolean> =
        context.settingsDataStore.data.map { it[SOUND_EFFECTS_ENABLED] ?: true }

    suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SOUND_EFFECTS_ENABLED] = enabled }
    }
}
