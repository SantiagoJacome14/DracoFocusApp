package co.edu.unab.dracofocusapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.repo.LessonProgressRepository
import co.edu.unab.dracofocusapp.domain.rewards.RewardManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LessonProgressViewModel(
    private val repository: LessonProgressRepository,
    private val rewardManager: RewardManager,
) : ViewModel() {

    val soloFundamentosProgress = repository.observeSoloFundamentosProgressFraction().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = 0f,
    )

    val soloFundamentosCompleted = repository.observeSoloFundamentosCompletedIds().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = emptySet(),
    )

    val museumCollectionFraction = repository.observeMuseumCollectionProgressFraction().stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = 0f,
    )

    /** True cuando están las 3 lecciones y el sobre sigue disponible sin reclamar fichas-flag. */
    val showEnvelopeHint = combine(
        repository.observeSoloFundamentosProgressFraction(),
        repository.observeEnvelopeClaimedFlag(),
    ) { progress, claimed ->
        progress >= 1f && !claimed
    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val envelopeUiEvents = MutableSharedFlow<RewardManager.EnvelopeOutcome>(extraBufferCapacity = 32)

    fun markLessonSucceeded(lessonId: String) {
        viewModelScope.launch {
            kotlin.runCatching { repository.markLessonCompleted(lessonId) }
        }
    }

    fun openSoloFundamentosEnvelope() {
        viewModelScope.launch {
            val outcome = rewardManager.openSoloFundamentosEnvelope()
            envelopeUiEvents.emit(outcome)
        }
    }

    companion object {
        fun factory(
            repository: LessonProgressRepository,
            rewardManager: RewardManager,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                require(modelClass.isAssignableFrom(LessonProgressViewModel::class.java))
                return LessonProgressViewModel(repository, rewardManager) as T
            }
        }
    }
}
