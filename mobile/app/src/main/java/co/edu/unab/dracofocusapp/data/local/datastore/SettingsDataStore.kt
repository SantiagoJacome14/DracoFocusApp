package co.edu.unab.dracofocusapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "app_settings")

/**
 * Preferencias generales de la app (notificaciones, sonido, etc.), persistidas
 * con DataStore para que sobrevivan a cerrar la app.
 */
class SettingsDataStore(private val context: Context) {

    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    }

    val notificationsEnabled: Flow<Boolean> =
        context.settingsDataStore.data.map { it[NOTIFICATIONS_ENABLED] ?: true }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[NOTIFICATIONS_ENABLED] = enabled }
    }
}
