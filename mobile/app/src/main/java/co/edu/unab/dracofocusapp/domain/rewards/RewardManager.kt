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
     * Uses the backend as source of truth: the server picks and stores the piece permanently.
     * Room is only a local cache — never the authoritative source for randomness.
     *
     * Returns PieceGranted only when the server assigns a brand-new piece.
     * Returns NotEligible if already claimed, offline, or collection is complete.
     */
    suspend fun grantLessonCompletionReward(userId: String, lessonSlug: String): EnvelopeOutcome {
        // Short-circuit: if locally cached as already claimed, skip the network call.
        val claimedSlugs = repository.snapshotClaimedSlugs(userId)
        if (lessonSlug in claimedSlugs) return EnvelopeOutcome.NotEligible

        val piece = repository.claimMuseumRewardFromServer(userId, lessonSlug)
        return if (piece != null) EnvelopeOutcome.PieceGranted(piece)
        else EnvelopeOutcome.NotEligible
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
