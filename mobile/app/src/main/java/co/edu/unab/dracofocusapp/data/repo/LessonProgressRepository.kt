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
import android.util.Log
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
        // 1. Determinar el slug (siempre preferimos slugs para persistencia local)
        val lessonIdInt = lessonId.toIntOrNull()
        val slug = if (lessonIdInt != null) {
            lessonRepository.getSlugById(lessonIdInt)
        } else {
            lessonId
        }

        val idToStore = slug ?: lessonId

        // 2. Local first using slug
        Log.d("PROGRESS_SYNC", "Guardado en Room slug=$idToStore (originalId=$lessonId)")
        lessonDao.upsert(
            CompletedLessonEntity(
                userId = userId,
                lessonId = idToStore,
                completedAtMillis = System.currentTimeMillis(),
            )
        )

        // 3. Sync to Laravel
        try {
            if (lessonRepository.ensureLessonsAvailable()) {
                val finalSlug = if (idToStore in SOLO_FUNDAMENTOS_SLUGS) idToStore else slug
                if (finalSlug != null) {
                    Log.d("PROGRESS_SYNC", "Enviando progreso: lesson=$finalSlug, score=100, completed=true")
                    apiService?.sendLessonProgress(ProgressRequest(lessonSlug = finalSlug, score = 100, completed = true))
                } else {
                    Log.e("PROGRESS_SYNC", "No slug found for lessonId: $lessonId, skipping sync")
                }
            } else {
                Log.e("PROGRESS_SYNC", "Lessons not available, skipping sync")
            }
        } catch (e: Exception) {
            Log.e("PROGRESS_SYNC", "Error al enviar progreso", e)
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
                Log.d("PROGRESS_SYNC", "Obteniendo progreso del servidor...")
                val response = apiService?.getProgress()
                Log.d("PROGRESS_SYNC", "Respuesta GET /api/progress: code=${response?.code()}")
                if (response != null && response.isSuccessful) {
                    val remoteProgress = response.body()?.data ?: emptyList()
                    Log.d("PROGRESS_SYNC", "Lecciones completadas desde servidor: ${remoteProgress.size}")
                    
                    remoteProgress.forEach { dto ->
                        val slugToStore = dto.lessonSlug
                        Log.d("PROGRESS_SYNC", "Guardado en Room slug=$slugToStore desde servidor")
                        lessonDao.upsert(
                            CompletedLessonEntity(
                                userId = userId,
                                lessonId = slugToStore,
                                completedAtMillis = dto.completedAt?.let { parseIsoDate(it) } ?: System.currentTimeMillis()
                            )
                        )
                    }
                    Log.d("PROGRESS_SYNC", "UI actualizada con progreso remoto")
                }
            } else {
                Log.e("PROGRESS_SYNC", "Lessons not available, skipping sync")
            }
        } catch (e: Exception) {
            Log.e("PROGRESS_SYNC", "Error en syncProgressFromServer", e)
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
