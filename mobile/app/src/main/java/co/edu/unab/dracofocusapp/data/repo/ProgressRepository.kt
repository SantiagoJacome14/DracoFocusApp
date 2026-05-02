package co.edu.unab.dracofocusapp.data.repo

import co.edu.unab.dracofocusapp.data.local.CompletedLessonEntity
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.ProgressRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime

/**
 * Repositorio de progreso (Versión compatible con multi-usuario).
 */
class ProgressRepository(
    private val db: DracoDatabase,
    private val apiService: ApiService,
    private val lessonRepository: LessonRepository? = null
) {
    private val lessonDao = db.completedLessonDao()

    /**
     * Observa las lecciones completadas desde Room filtrando por usuario.
     */
    fun observeCompletedLessons(userId: String): Flow<Set<String>> = 
        lessonDao.observeCompletedIds(userId).map { it.toSet() }

    /**
     * Marca una lección como completada localmente y luego intenta sincronizar con el servidor.
     */
    suspend fun markLessonCompleted(userId: String, lessonId: String, score: Int = 100): Result<Unit> {
        // 1. Guardar localmente siempre usando el userId
        lessonDao.upsert(
            CompletedLessonEntity(
                userId = userId,
                lessonId = lessonId,
                completedAtMillis = System.currentTimeMillis()
            )
        )

        // 2. Intentar enviar al servidor mapeando a slug dinámicamente
        return try {
            val lessonIdInt = lessonId.toIntOrNull()
            val slug = lessonIdInt?.let { lessonRepository?.getSlugById(it) }
            if (slug != null) {
                val response = apiService.sendLessonProgress(ProgressRequest(lessonSlug = slug, score = score))
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al sincronizar con el servidor: ${response.code()}"))
                }
            } else {
                Result.failure(Exception("No se pudo mapear lessonId $lessonId a slug"))
            }
        } catch (e: Exception) {
            // Manejar error de red, pero los datos ya están en Room
            Result.failure(e)
        }
    }

    /**
     * Trae todo el progreso del servidor y actualiza la base de datos local (Sync).
     */
    suspend fun syncProgressFromServer(userId: String): Result<Unit> {
        return try {
            val response = apiService.getProgress()
            if (response.isSuccessful && response.body() != null) {
                val remoteProgress = response.body()!!.data
                
                remoteProgress.forEach { dto ->
                    val localId = lessonRepository?.getIdBySlug(dto.lessonId)?.toString() ?: dto.lessonId
                    lessonDao.upsert(
                        CompletedLessonEntity(
                            userId = userId,
                            lessonId = localId,
                            completedAtMillis = parseIsoDate(dto.completedAt)
                        )
                    )
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al obtener progreso: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
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
