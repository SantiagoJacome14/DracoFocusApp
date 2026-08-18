package co.edu.unab.dracofocusapp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "pomodoro_prefs")

class PomodoroDataStore(private val context: Context) {

    companion object {
        private val STATUS = stringPreferencesKey("timer_status")
        private val MODE = stringPreferencesKey("timer_mode")
        private val EXPECTED_END_TIME = longPreferencesKey("expected_end_time")
        private val REMAINING_TIME_PAUSE = longPreferencesKey("remaining_time_pause")
        private val WORK_DURATION = intPreferencesKey("work_duration_mins")
        private val REST_DURATION = intPreferencesKey("rest_duration_mins")
    }

    val pomodoroState: Flow<PomodoroStatePrefs> = context.dataStore.data.map { prefs ->
        PomodoroStatePrefs(
            status = prefs[STATUS] ?: "IDLE",
            mode = prefs[MODE] ?: "WORK",
            expectedEndTime = prefs[EXPECTED_END_TIME] ?: 0L,
            remainingTimeAtPause = prefs[REMAINING_TIME_PAUSE] ?: 0L,
            workDurationMins = prefs[WORK_DURATION] ?: 25,
            restDurationMins = prefs[REST_DURATION] ?: 5
        )
    }

    suspend fun saveState(state: PomodoroStatePrefs) {
        context.dataStore.edit { prefs ->
            prefs[STATUS] = state.status
            prefs[MODE] = state.mode
            prefs[EXPECTED_END_TIME] = state.expectedEndTime
            prefs[REMAINING_TIME_PAUSE] = state.remainingTimeAtPause
            prefs[WORK_DURATION] = state.workDurationMins
            prefs[REST_DURATION] = state.restDurationMins
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}

data class PomodoroStatePrefs(
    val status: String,
    val mode: String,
    val expectedEndTime: Long,
    val remainingTimeAtPause: Long,
    val workDurationMins: Int,
    val restDurationMins: Int
)
