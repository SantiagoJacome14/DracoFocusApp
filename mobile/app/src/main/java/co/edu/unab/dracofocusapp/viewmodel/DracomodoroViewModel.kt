package co.edu.unab.dracofocusapp.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.local.datastore.PomodoroDataStore
import co.edu.unab.dracofocusapp.data.local.datastore.PomodoroStatePrefs
import co.edu.unab.dracofocusapp.domain.util.Clock
import co.edu.unab.dracofocusapp.util.PomodoroNotifier
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import android.util.Log

enum class PomodoroStatus { IDLE, RUNNING, PAUSED, COMPLETED }
enum class PomodoroMode { WORK, BREAK }

data class DracomodoroUiState(
    val status: PomodoroStatus = PomodoroStatus.IDLE,
    val mode: PomodoroMode = PomodoroMode.WORK,
    val secondsLeft: Int = 25 * 60,
    val workMinutes: Int = 25,
    val restMinutes: Int = 5,
    val isInitialized: Boolean = false
)

class DracomodoroViewModel(
    private val dataStore: PomodoroDataStore,
    private val clock: Clock,
    private val appContext: Context
) : ViewModel() {

    var uiState by mutableStateOf(DracomodoroUiState())
        private set

    private var timerJob: Job? = null

    init {
        restoreState()
    }

    private fun restoreState() {
        viewModelScope.launch {
            val saved = dataStore.pomodoroState.first()
            val status = try { PomodoroStatus.valueOf(saved.status) } catch (e: Exception) { PomodoroStatus.IDLE }
            val mode = try { PomodoroMode.valueOf(saved.mode) } catch (e: Exception) { PomodoroMode.WORK }
            
            uiState = uiState.copy(
                status = status,
                mode = mode,
                workMinutes = saved.workDurationMins,
                restMinutes = saved.restDurationMins,
                isInitialized = true
            )

            when (status) {
                PomodoroStatus.RUNNING -> {
                    val now = clock.currentTimeMillis()
                    if (now >= saved.expectedEndTime) {
                        handleTimerFinished(mode)
                    } else {
                        val remaining = ((saved.expectedEndTime - now) / 1000).toInt()
                        uiState = uiState.copy(secondsLeft = remaining)
                        startTicking(saved.expectedEndTime)
                    }
                }
                PomodoroStatus.PAUSED -> {
                    uiState = uiState.copy(secondsLeft = (saved.remainingTimeAtPause / 1000).toInt())
                }
                else -> {
                    uiState = uiState.copy(secondsLeft = if (mode == PomodoroMode.WORK) saved.workDurationMins * 60 else saved.restDurationMins * 60)
                }
            }
        }
    }

    fun onStart() {
        if (uiState.status == PomodoroStatus.RUNNING) return
        
        val durationMillis = uiState.secondsLeft * 1000L
        val expectedEndTime = clock.currentTimeMillis() + durationMillis
        
        uiState = uiState.copy(status = PomodoroStatus.RUNNING)
        saveToDataStore(expectedEndTime = expectedEndTime)
        startTicking(expectedEndTime)
    }

    fun onPause() {
        if (uiState.status != PomodoroStatus.RUNNING) return
        timerJob?.cancel()
        
        val remainingMillis = uiState.secondsLeft * 1000L
        uiState = uiState.copy(status = PomodoroStatus.PAUSED)
        saveToDataStore(remainingTimeAtPause = remainingMillis)
    }

    fun onResume() {
        if (uiState.status != PomodoroStatus.PAUSED) return
        onStart() // Recalcula expectedEndTime basado en secondsLeft actual
    }

    fun onReset() {
        timerJob?.cancel()
        val resetSeconds = if (uiState.mode == PomodoroMode.WORK) uiState.workMinutes * 60 else uiState.restMinutes * 60
        uiState = uiState.copy(status = PomodoroStatus.IDLE, secondsLeft = resetSeconds)
        saveToDataStore(expectedEndTime = 0, remainingTimeAtPause = 0)
    }

    /**
     * Salta la fase actual (trabajo o descanso) y arranca directo la otra.
     * Es silencioso (sin notificación ni crédito de estudio): el usuario ya sabe que lo saltó,
     * y se puede usar en cualquier modo, las veces que haga falta.
     */
    fun onSkipPhase() {
        timerJob?.cancel()

        val nextMode = if (uiState.mode == PomodoroMode.WORK) PomodoroMode.BREAK else PomodoroMode.WORK
        val nextSeconds = if (nextMode == PomodoroMode.WORK) uiState.workMinutes * 60 else uiState.restMinutes * 60
        val nextExpectedEndTime = clock.currentTimeMillis() + nextSeconds * 1000L

        uiState = uiState.copy(
            mode = nextMode,
            status = PomodoroStatus.RUNNING,
            secondsLeft = nextSeconds
        )
        saveToDataStore(expectedEndTime = nextExpectedEndTime, remainingTimeAtPause = 0)
        startTicking(nextExpectedEndTime)
    }

    fun adjustWorkMinutes(delta: Int) {
        if (uiState.status != PomodoroStatus.IDLE) return
        val newValue = (uiState.workMinutes + delta).coerceIn(1, 60)
        uiState = uiState.copy(workMinutes = newValue)
        if (uiState.mode == PomodoroMode.WORK) {
            uiState = uiState.copy(secondsLeft = newValue * 60)
        }
        saveToDataStore()
    }

    fun adjustRestMinutes(delta: Int) {
        if (uiState.status != PomodoroStatus.IDLE) return
        val newValue = (uiState.restMinutes + delta).coerceIn(1, 60)
        uiState = uiState.copy(restMinutes = newValue)
        if (uiState.mode == PomodoroMode.BREAK) {
            uiState = uiState.copy(secondsLeft = newValue * 60)
        }
        saveToDataStore()
    }

    private fun startTicking(targetTime: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = clock.currentTimeMillis()
                val remaining = ((targetTime - now) / 1000).toInt().coerceAtLeast(0)
                uiState = uiState.copy(secondsLeft = remaining)
                
                if (remaining <= 0) {
                    handleTimerFinished(uiState.mode)
                    break
                }
                delay(500L) // Refresco frecuente para fluidez, pero el cálculo es via timestamp
            }
        }
    }

    private fun handleTimerFinished(mode: PomodoroMode) {
        timerJob?.cancel()

        viewModelScope.launch {
            if (mode == PomodoroMode.WORK) {
                registrarEstudioEnFirebase()
            }

            // Cambio automático de modo: el ciclo sigue solo, como un Pomodoro real
            val nextMode = if (mode == PomodoroMode.WORK) PomodoroMode.BREAK else PomodoroMode.WORK
            val nextSeconds = if (nextMode == PomodoroMode.WORK) uiState.workMinutes * 60 else uiState.restMinutes * 60
            val nextExpectedEndTime = clock.currentTimeMillis() + nextSeconds * 1000L

            val (title, message) = if (mode == PomodoroMode.WORK) {
                "¡Sesión de trabajo completada! 🎉" to "Hora de descansar ${uiState.restMinutes} min."
            } else {
                "¡Descanso terminado! 💪" to "Volvamos a estudiar ${uiState.workMinutes} min."
            }
            PomodoroNotifier.notifyPhaseFinished(appContext, title, message)

            uiState = uiState.copy(
                mode = nextMode,
                status = PomodoroStatus.RUNNING,
                secondsLeft = nextSeconds
            )
            saveToDataStore(expectedEndTime = nextExpectedEndTime, remainingTimeAtPause = 0)
            startTicking(nextExpectedEndTime)
        }
    }

    private fun saveToDataStore(
        expectedEndTime: Long? = null,
        remainingTimeAtPause: Long? = null
    ) {
        viewModelScope.launch {
            val currentState = dataStore.pomodoroState.first()
            dataStore.saveState(
                PomodoroStatePrefs(
                    status = uiState.status.name,
                    mode = uiState.mode.name,
                    expectedEndTime = expectedEndTime ?: currentState.expectedEndTime,
                    remainingTimeAtPause = remainingTimeAtPause ?: currentState.remainingTimeAtPause,
                    workDurationMins = uiState.workMinutes,
                    restDurationMins = uiState.restMinutes
                )
            )
        }
    }

    /**
     * Lógica original de Firebase movida al ViewModel. 
     * Se mantiene idéntica para no romper funcionalidad en Fase 1.
     */
    private fun registrarEstudioEnFirebase() {
        try {
            val db = FirebaseFirestore.getInstance()
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val usuarioRef = db.collection("usuarios_estadisticas").document(userId)
            usuarioRef.update(
                mapOf(
                    "cantidad_estudio" to FieldValue.increment(1),
                    "minutos_estudiados_semanal" to FieldValue.increment(uiState.workMinutes.toDouble()),
                    "horas_estudio" to FieldValue.increment(uiState.workMinutes / 60.0),
                    "dia" to FieldValue.serverTimestamp()
                )
            ).addOnSuccessListener {
                Log.d("POMODORO_SYNC", "Estadísticas actualizadas con éxito")
            }.addOnFailureListener { e ->
                Log.e("POMODORO_SYNC", "Error al actualizar estadísticas", e)
            }
        } catch (e: Exception) {
            Log.e("POMODORO_SYNC", "Fallo crítico en Firebase", e)
        }
    }
}
