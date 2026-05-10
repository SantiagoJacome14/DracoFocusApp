package co.edu.unab.dracofocusapp.data.repo

import androidx.room.withTransaction
import co.edu.unab.dracofocusapp.data.local.CompletedLessonEntity
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.local.LessonRewardClaimEntity
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
    private val claimsDao get() = db.lessonRewardClaimsDao()

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

    /** Upsert local inmediato — no hace red. Llámalo ANTES de navegar para que el Flow emita al instante. */
    suspend fun markLessonCompletedLocalOnly(userId: String, lessonSlug: String) {
        lessonDao.upsert(
            CompletedLessonEntity(
                userId = userId,
                lessonId = lessonSlug,
                completedAtMillis = System.currentTimeMillis(),
            )
        )
        Log.d("PROGRESS_SYNC", "markLessonCompletedLocalOnly: userId=$userId slug=$lessonSlug")
    }

    /** Sincroniza con backend y retorna XP ganado (0 si la lección ya estaba completada o falla). */
    suspend fun markLessonCompleted(userId: String, lessonId: String): Int {
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

        // 2. Guardar local (también lo hace markLessonCompletedLocalOnly antes de navegar, pero por si acaso)
        lessonDao.upsert(
            CompletedLessonEntity(
                userId = userId,
                lessonId = idToStore,
                completedAtMillis = System.currentTimeMillis(),
            )
        )

        // 3. Enviar TODOS los slugs locales al servidor
        var xpEarned = 0
        try {
            val localSlugs = lessonDao.snapshotLessonIds(userId)
            Log.d("PROGRESS_SYNC", "Enviando sync con ${localSlugs.size} slugs: $localSlugs")
            val syncResponse = apiService?.syncProgress(SyncProgressRequest(completedLessons = localSlugs))
            if (syncResponse != null && syncResponse.isSuccessful && syncResponse.body() != null) {
                val body = syncResponse.body()!!
                val serverSlugs = body.completedLessons
                xpEarned = body.xpEarned
                Log.d("PROGRESS_SYNC", "Respuesta sync: slugs=$serverSlugs xpEarned=$xpEarned totalXp=${body.totalXp}")
                // Transacción atómica: evita parpadeo en la UI entre clear e inserts
                db.withTransaction {
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
                // Invalidar cache de perfil para que muestre XP actualizado
                if (xpEarned > 0) {
                    Log.d("PROGRESS_SYNC", "XP ganado: +$xpEarned → invalidando cache perfil")
                }
            }
        } catch (e: Exception) {
            Log.e("PROGRESS_SYNC", "Error al enviar sync", e)
        }
        return xpEarned
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
     * Claims a lesson reward slot. Returns true if newly claimed, false if already claimed.
     * Uses OnConflict IGNORE so a duplicate insert returns -1.
     */
    suspend fun claimLessonForReward(userId: String, lessonSlug: String): Boolean =
        claimsDao.insertClaim(
            LessonRewardClaimEntity(userId, lessonSlug, System.currentTimeMillis())
        ) != -1L

    /**
     * Picks a random unlocked piece from the catalog and saves it to museum_unlocks.
     * Returns the piece, or null if the collection is already complete.
     */
    suspend fun unlockRandomPiece(userId: String): MuseumCatalog.Piece? {
        val alreadyUnlocked = museumDao.snapshotUnlockedPieceIds(userId).toSet()
        val pending = MuseumCatalog.ALL_PIECES.filter { it.catalogId !in alreadyUnlocked }
        if (pending.isEmpty()) return null
        val piece = pending[kotlin.random.Random.nextInt(pending.size)]
        insertMuseumPiece(userId, piece.catalogId)
        Log.d("MUSEUM", "unlockRandomPiece: userId=$userId → ${piece.catalogId} (${piece.title})")
        return piece
    }

    /**
     * Returns completed lesson slugs that have not yet generated a museum reward claim.
     * Used to grant pending rewards for lessons completed on Web.
     */
    suspend fun getUnclaimedCompletedSlugs(userId: String): List<String> {
        val completed = lessonDao.snapshotLessonIds(userId)
        val claimed = claimsDao.getClaimedSlugs(userId).toSet()
        return completed.filter { it !in claimed }
    }

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
                // Transacción atómica: observers solo reciben 1 notificación con el estado final
                db.withTransaction {
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
                }
                Log.d("PROGRESS_SYNC", "UI actualizada con progreso remoto")
            }
        } catch (e: Exception) {
            Log.e("PROGRESS_SYNC", "Error en syncProgressFromServer", e)
        }
    }
}
