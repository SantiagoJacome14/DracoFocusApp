package co.edu.unab.dracofocusapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.edu.unab.dracofocusapp.data.remote.UserStatsDto
import com.google.gson.Gson
import kotlinx.coroutines.flow.first

private val Context.avancesCacheDataStore by preferencesDataStore(name = "avances_cache")

/**
 * Cache en DISCO (sobrevive a cerrar la app por completo) del último /api/user/stats
 * exitoso. El cache en memoria (AvancesViewModel.memoryCache) solo dura mientras el
 * proceso vive; este permite mostrar Avances al instante incluso en una apertura nueva
 * de la app, mientras se refresca en segundo plano.
 */
class AvancesCacheDataStore(private val context: Context) {

    companion object {
        private val STATS_JSON = stringPreferencesKey("stats_json")
    }

    private val gson = Gson()

    suspend fun getCachedStats(): UserStatsDto? {
        val json = context.avancesCacheDataStore.data.first()[STATS_JSON] ?: return null
        return try {
            gson.fromJson(json, UserStatsDto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveStats(stats: UserStatsDto) {
        context.avancesCacheDataStore.edit { it[STATS_JSON] = gson.toJson(stats) }
    }

    suspend fun clear() {
        context.avancesCacheDataStore.edit { it.remove(STATS_JSON) }
    }
}
