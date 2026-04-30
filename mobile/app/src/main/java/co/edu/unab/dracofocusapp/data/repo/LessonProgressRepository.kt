package co.edu.unab.dracofocusapp.data.repo

import co.edu.unab.dracofocusapp.data.local.CompletedLessonEntity
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.local.MuseumUnlockEntity
import co.edu.unab.dracofocusapp.data.local.RewardFlagsEntity
import co.edu.unab.dracofocusapp.museum.MuseumCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Persistencia local: lecciones completadas, flags de sobres y desbloqueos del museo.
 */
class LessonProgressRepository(private val db: DracoDatabase) {

    companion object {
        /** Set “Fundamentos”: las 3 misiones del menú Draco Solitario. */
        val SOLO_FUNDAMENTOS_IDS = setOf("1", "2", "3")
        private const val REWARD_ROW_ID = 0
    }

    private val lessonDao get() = db.completedLessonDao()
    private val flagsDao get() = db.rewardFlagsDao()
    private val museumDao get() = db.museumUnlockDao()

    fun observeSoloFundamentosCompletedIds(): Flow<Set<String>> =
        lessonDao.observeCompletedIds().map { ids ->
            ids.filter { it in SOLO_FUNDAMENTOS_IDS }.toSet()
        }

    fun observeSoloFundamentosProgressFraction(): Flow<Float> =
        observeSoloFundamentosCompletedIds().map { done ->
            (done.size.coerceAtMost(SOLO_FUNDAMENTOS_IDS.size)).toFloat() /
                SOLO_FUNDAMENTOS_IDS.size.toFloat()
        }

    fun observeEnvelopeClaimedFlag(): Flow<Boolean> =
        flagsDao.observe(REWARD_ROW_ID).map { it?.soloFundamentosEnvelopeClaimed ?: false }

    suspend fun markLessonCompleted(lessonId: String) {
        lessonDao.upsert(
            CompletedLessonEntity(
                lessonId = lessonId,
                completedAtMillis = System.currentTimeMillis(),
            )
        )
    }

    suspend fun shouldShowSobreMisterioso(): Boolean {
        val ids = lessonDao.snapshotLessonIds()
            .filter { it in SOLO_FUNDAMENTOS_IDS }
            .toSet()
        val envelopeClaimed =
            flagsDao.snapshot(REWARD_ROW_ID)?.soloFundamentosEnvelopeClaimed ?: false
        return ids.containsAll(SOLO_FUNDAMENTOS_IDS) && !envelopeClaimed
    }

    suspend fun markSoloEnvelopeClaimed() {
        flagsDao.upsert(RewardFlagsEntity(id = REWARD_ROW_ID, soloFundamentosEnvelopeClaimed = true))
    }

    fun observeUnlockedPieceIds(): Flow<Set<String>> =
        museumDao.observeUnlockedPieceIds().map { it.toSet() }

    fun observeMuseumCollectionProgressFraction(): Flow<Float> {
        val totalCatalog = MuseumCatalog.ALL_PIECES.size
        return museumDao.observeCollectedCount().map { count ->
            (count.coerceAtMost(totalCatalog)).toFloat() / totalCatalog.toFloat()
        }
    }

    suspend fun insertMuseumPiece(pieceId: String) {
        museumDao.upsert(
            MuseumUnlockEntity(
                pieceCatalogId = pieceId,
                unlockedAtMillis = System.currentTimeMillis(),
            )
        )
    }

    suspend fun snapshotUnlockedPieceIdsBlocking(): Set<String> =
        museumDao.snapshotUnlockedPieceIds().toSet()
}
