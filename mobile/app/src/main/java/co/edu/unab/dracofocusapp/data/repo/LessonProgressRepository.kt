package co.edu.unab.dracofocusapp.data.repo

import co.edu.unab.dracofocusapp.data.local.CompletedLessonEntity
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.local.MuseumUnlockEntity
import co.edu.unab.dracofocusapp.data.local.RewardFlagsEntity
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.SyncProgressRequest
import co.edu.unab.dracofocusapp.museum.MuseumCatalog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.util.Log

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
        /** Set "Fundamentos": las 3 misiones del menú Draco Solitario (usar slugs, no IDs de Laravel). */
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
        // 1. Determinar el slug
        val lessonIdInt = lessonId.toIntOrNull()
        val slug = if (lessonIdInt != null) {
            lessonRepository.ensureLessonsAvailable()
            lessonRepository.getSlugById(lessonIdInt)
        } else {
            lessonId
        }

        val idToStore = slug ?: lessonId
        Log.d("PROGRESS_SYNC", "markLessonCompleted: originalId=$lessonId -> idToStore=$idToStore")

        // 2. Guardar local
        Log.d("PROGRESS_SYNC", "Guardado en Room slug=$idToStore (originalId=$lessonId)")
        lessonDao.upsert(
            CompletedLessonEntity(
                userId = userId,
                lessonId = idToStore,
                completedAtMillis = System.currentTimeMillis(),
            )
        )

        // 3. Enviar TODOS los slugs locales al servidor
        try {
            val localSlugs = lessonDao.snapshotLessonIds(userId)
            Log.d("PROGRESS_SYNC", "Enviando sync con ${localSlugs.size} slugs: $localSlugs")
            val syncResponse = apiService?.syncProgress(SyncProgressRequest(completedLessons = localSlugs))
            if (syncResponse != null && syncResponse.isSuccessful && syncResponse.body() != null) {
                val serverSlugs = syncResponse.body()!!.completedLessons
                Log.d("PROGRESS_SYNC", "Respuesta sync del servidor: $serverSlugs")
                lessonDao.clearForUser(userId)
                serverSlugs.forEach { serverSlug ->
                    lessonDao.upsert(
                        CompletedLessonEntity(
                            userId = userId,
                            lessonId = serverSlug,
                            completedAtMillis = System.currentTimeMillis(),
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("PROGRESS_SYNC", "Error al enviar sync", e)
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
     * Sincroniza el progreso desde el servidor hacia Room.
     * Usa GET /api/progress, borra progreso local y reemplaza con la respuesta del servidor.
     */
    suspend fun syncProgressFromServer(userId: String) {
        try {
            Log.d("PROGRESS_SYNC", "Obteniendo progreso del servidor...")
            val response = apiService?.getProgress()
            Log.d("PROGRESS_SYNC", "GET /api/progress response: code=${response?.code()} body=${response?.body()}")
            if (response != null && response.isSuccessful && response.body() != null) {
                val serverSlugs = response.body()!!.completedLessons.map { it.trim() }
                Log.d("PROGRESS_SYNC", "Slugs recibidos y limpios del backend: $serverSlugs")

                lessonDao.clearForUser(userId)
                serverSlugs.forEach { serverSlug ->
                    Log.d("PROGRESS_ROOM", "Guardando en Room userId=$userId slug=$serverSlug")
                    lessonDao.upsert(
                        CompletedLessonEntity(
                            userId = userId,
                            lessonId = serverSlug,
                            completedAtMillis = System.currentTimeMillis()
                        )
                    )
                }
                Log.d("PROGRESS_SYNC", "UI actualizada con progreso remoto")
            }
        } catch (e: Exception) {
            Log.e("PROGRESS_SYNC", "Error en syncProgressFromServer", e)
        }
    }
}
