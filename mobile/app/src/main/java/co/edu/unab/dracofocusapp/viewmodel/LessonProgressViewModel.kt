package co.edu.unab.dracofocusapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.repo.LessonProgressRepository
import co.edu.unab.dracofocusapp.domain.rewards.RewardManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import android.util.Log

sealed class SyncState {
    object Idle : SyncState()
    object Syncing : SyncState()
    object Synced : SyncState()
    data class Error(val message: String) : SyncState()
}
class LessonProgressViewModel(
    private val userId: String,
    private val repository: LessonProgressRepository,
    private val rewardManager: RewardManager,
) : ViewModel() {

    init {
        Log.d("PROGRESS_SYNC", "LessonProgressViewModel inicializado para userId=$userId")
    }

    val soloFundamentosProgress = repository.observeSoloFundamentosProgressFraction(userId).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = 0f,
    )

    val soloFundamentosCompleted = repository.observeSoloFundamentosCompletedIds(userId)
        .onEach { Log.d("PROGRESS_SYNC", "soloFundamentosCompleted emitió: $it") }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            initialValue = emptySet(),
        )

    val museumCollectionFraction = repository.observeMuseumCollectionProgressFraction(userId).stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = 0f,
    )

    /** True cuando están las 3 lecciones y el sobre sigue disponible sin reclamar fichas-flag. */
    val showEnvelopeHint = combine(
        repository.observeSoloFundamentosProgressFraction(userId),
        repository.observeEnvelopeClaimedFlag(userId),
    ) { progress, claimed ->
        progress >= 1f && !claimed
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val envelopeUiEvents = MutableSharedFlow<RewardManager.EnvelopeOutcome>(extraBufferCapacity = 32)
    private val _syncState = androidx.compose.runtime.mutableStateOf<SyncState>(SyncState.Idle)
    val syncState: androidx.compose.runtime.State<SyncState> = _syncState

    fun refreshProgress() {
    viewModelScope.launch {
        _syncState.value = SyncState.Syncing
        Log.d("PROGRESS_SYNC", "refreshProgress ejecutado")

        try {
            repository.syncProgressFromServer(userId)
            _syncState.value = SyncState.Synced
        } catch (e: Exception) {
            _syncState.value = SyncState.Error("Error al sincronizar")
        }
    }
}

    fun markLessonSucceeded(lessonId: String) {
    viewModelScope.launch {
        _syncState.value = SyncState.Syncing

        try {
            repository.markLessonCompleted(userId, lessonId)
            _syncState.value = SyncState.Synced
        } catch (e: Exception) {
            _syncState.value = SyncState.Error("No se pudo sincronizar")
        }
    }
}

    fun openSoloFundamentosEnvelope() {
        viewModelScope.launch {
            // RewardManager también necesita userId ahora si persiste algo
            val outcome = rewardManager.openSoloFundamentosEnvelope(userId)
            envelopeUiEvents.emit(outcome)
        }
    }

    companion object {
        fun factory(
            userId: String,
            repository: LessonProgressRepository,
            rewardManager: RewardManager,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(LessonProgressViewModel::class.java))
                return LessonProgressViewModel(userId, repository, rewardManager) as T
            }
        }
    }
}
