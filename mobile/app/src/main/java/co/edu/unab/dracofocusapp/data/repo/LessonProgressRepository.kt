package co.edu.unab.dracofocusapp.data.repo

import co.edu.unab.dracofocusapp.data.local.CompletedLessonEntity
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.local.MuseumUnlockEntity
import co.edu.unab.dracofocusapp.data.local.RewardFlagsEntity
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.ProgressRequest
import co.edu.unab.dracofocusapp.museum.MuseumCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

/**
 * Persistencia local: lecciones completadas, flags de sobres y desbloqueos del museo.
 * Sincronizado con Laravel y aislado por userId.
 */
class LessonProgressRepository(
    private val db: DracoDatabase,
    private val apiService: ApiService? = null,
    private val lessonRepository: LessonRepository
) {

    companion object {
        /** Set “Fundamentos”: las 3 misiones del menú Draco Solitario (usar slugs, no IDs de Laravel). */
        val SOLO_FUNDAMENTOS_SLUGS = setOf("decisiones_de_fuego", "vuelo_infinito", "el_libro_de_tareas")
    }

    private val lessonDao get() = db.completedLessonDao()
    private val flagsDao get() = db.rewardFlagsDao()
    private val museumDao get() = db.museumUnlockDao()

    fun observeSoloFundamentosCompletedIds(userId: String): Flow<Set<String>> =
        lessonDao.observeCompletedIds(userId).map { ids ->
            ids.filter { it in SOLO_FUNDAMENTOS_SLUGS }.toSet()
        }

    fun observeSoloFundamentosProgressFraction(userId: String): Flow<Float> =
        observeSoloFundamentosCompletedIds(userId).map { done ->
            (done.size.coerceAtMost(SOLO_FUNDAMENTOS_SLUGS.size)).toFloat() /
                SOLO_FUNDAMENTOS_SLUGS.size.toFloat()
        }

    fun observeEnvelopeClaimedFlag(userId: String): Flow<Boolean> =
        flagsDao.observe(userId).map { it?.soloFundamentosEnvelopeClaimed ?: false }

    suspend fun markLessonCompleted(userId: String, lessonId: String) {
        // 1. Local first
        lessonDao.upsert(
            CompletedLessonEntity(
                userId = userId,
                lessonId = lessonId,
                completedAtMillis = System.currentTimeMillis(),
            )
        )

        // 2. Sync to Laravel (only if lessons are available)
        try {
            if (lessonRepository.ensureLessonsAvailable()) {
                val lessonIdInt = lessonId.toIntOrNull()
                val slug = lessonIdInt?.let { lessonRepository.getSlugById(it) }
                if (slug != null) {
                    apiService?.sendLessonProgress(ProgressRequest(lessonSlug = slug, score = 100))
                } else {
                    android.util.Log.e("LessonProgressRepo", "No slug found for lessonId: $lessonId, skipping sync")
                }
            } else {
                android.util.Log.e("LessonProgressRepo", "Lessons not available, skipping sync")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun shouldShowSobreMisterioso(userId: String): Boolean {
        val ids = lessonDao.snapshotLessonIds(userId)
            .filter { it in SOLO_FUNDAMENTOS_SLUGS }
            .toSet()
        val envelopeClaimed =
            flagsDao.snapshot(userId)?.soloFundamentosEnvelopeClaimed ?: false
        return ids.containsAll(SOLO_FUNDAMENTOS_SLUGS) && !envelopeClaimed
    }

    suspend fun markSoloEnvelopeClaimed(userId: String) {
        flagsDao.upsert(RewardFlagsEntity(userId = userId, soloFundamentosEnvelopeClaimed = true))
    }

    fun observeUnlockedPieceIds(userId: String): Flow<Set<String>> =
        museumDao.observeUnlockedPieceIds(userId).map { it.toSet() }

    fun observeMuseumCollectionProgressFraction(userId: String): Flow<Float> {
        val totalCatalog = MuseumCatalog.ALL_PIECES.size
        return museumDao.observeCollectedCount(userId).map { count ->
            (count.coerceAtMost(totalCatalog)).toFloat() / totalCatalog.toFloat()
        }
    }

    suspend fun insertMuseumPiece(userId: String, pieceId: String) {
        museumDao.upsert(
            MuseumUnlockEntity(
                userId = userId,
                pieceCatalogId = pieceId,
                unlockedAtMillis = System.currentTimeMillis(),
            )
        )
    }

    suspend fun snapshotUnlockedPieceIdsBlocking(userId: String): Set<String> =
        museumDao.snapshotUnlockedPieceIds(userId).toSet()

    /**
     * Sincroniza el progreso desde el servidor hacia Room para un usuario específico.
     */
    suspend fun syncProgressFromServer(userId: String) {
        try {
            if (lessonRepository.ensureLessonsAvailable()) {
                val response = apiService?.getProgress()
                if (response != null && response.isSuccessful) {
                    // Opcional: limpiar progreso local del usuario antes de re-sincronizar
                    // lessonDao.clearForUser(userId) 
                    
                    response.body()?.data?.forEach { dto ->
                        val localId = lessonRepository.getIdBySlug(dto.lessonId)?.toString() ?: dto.lessonId
                        lessonDao.upsert(
                            CompletedLessonEntity(
                                userId = userId,
                                lessonId = localId,
                                completedAtMillis = parseIsoDate(dto.completedAt)
                            )
                        )
                    }
                }
            } else {
                android.util.Log.e("LessonProgressRepo", "Lessons not available, skipping sync")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseIsoDate(isoDate: String): Long {
        return try {
            OffsetDateTime.parse(isoDate).toInstant().toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}
