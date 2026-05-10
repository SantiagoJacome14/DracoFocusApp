package co.edu.unab.dracofocusapp.domain.rewards

import co.edu.unab.dracofocusapp.data.repo.LessonProgressRepository
import co.edu.unab.dracofocusapp.museum.MuseumCatalog

/**
 * Lógica de sobres misteriosos y entrega controlada de fichas al museo.
 */
class RewardManager(
    private val repository: LessonProgressRepository,
    private val random: kotlin.random.Random = kotlin.random.Random.Default,
) {

    sealed interface EnvelopeOutcome {
        data class PieceGranted(val piece: MuseumCatalog.Piece) : EnvelopeOutcome
        data object NotEligible : EnvelopeOutcome
        data object NoPiecesLeftInCatalog : EnvelopeOutcome
    }

    /**
     * Called after completing a normal lesson (not review mode).
     * Claims the reward slot for that lesson (once ever, per user).
     * If newly claimed, picks and saves a random unlocked museum piece.
     */
    suspend fun grantLessonCompletionReward(userId: String, lessonSlug: String): EnvelopeOutcome {
        val newlyClaimed = repository.claimLessonForReward(userId, lessonSlug)
        if (!newlyClaimed) return EnvelopeOutcome.NotEligible

        val piece = repository.unlockRandomPiece(userId)
        return if (piece != null) EnvelopeOutcome.PieceGranted(piece)
        else EnvelopeOutcome.NoPiecesLeftInCatalog
    }

    /**
     * Al tocar el sobre: valida prerequisitos, desbloquea una ficha y marca el sobre como abierto localmente.
     */
    suspend fun openSoloFundamentosEnvelope(userId: String): EnvelopeOutcome {
        if (!repository.shouldShowSobreMisterioso(userId)) return EnvelopeOutcome.NotEligible

        val unlocked = repository.snapshotUnlockedPieceIdsBlocking(userId)
        val pending = MuseumCatalog.ALL_PIECES.filter { it.catalogId !in unlocked }
        if (pending.isEmpty()) return EnvelopeOutcome.NoPiecesLeftInCatalog

        val piece = pending[random.nextInt(pending.size)]
        repository.insertMuseumPiece(userId, piece.catalogId)
        repository.markSoloEnvelopeClaimed(userId)
        return EnvelopeOutcome.PieceGranted(piece)
    }
}
