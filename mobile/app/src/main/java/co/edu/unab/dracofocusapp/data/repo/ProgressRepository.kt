package co.edu.unab.dracofocusapp.data.repo

import co.edu.unab.dracofocusapp.data.local.CompletedLessonEntity
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.ProgressRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import android.util.Log
import java.time.OffsetDateTime

/**
 * Repositorio de progreso (Versión compatible con multi-usuario).
 */
class ProgressRepository(
    private val db: DracoDatabase,
    private val apiService: ApiService,
    private val lessonRepository: LessonRepository
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
            if (lessonRepository.ensureLessonsAvailable()) {
                val lessonIdInt = lessonId.toIntOrNull()
                val slug = lessonIdInt?.let { lessonRepository.getSlugById(it) }
                if (slug != null) {
                    Log.d("PROGRESS_SYNC", "Enviando progreso: lesson=$slug, score=$score, completed=true")
                    val response = apiService.sendLessonProgress(ProgressRequest(lessonSlug = slug, score = score, completed = true))
                    Log.d("PROGRESS_SYNC", "Respuesta POST /api/progress: code=${response.code()}, isSuccess=${response.isSuccessful}")
                    if (response.isSuccessful) {
                        Result.success(Unit)
                    } else {
                        Result.failure(Exception("Error al sincronizar con el servidor: ${response.code()}"))
                    }
                } else {
                    android.util.Log.e("ProgressRepo", "No slug found for lessonId: $lessonId, skipping sync")
                    Result.success(Unit) // No romper progreso, solo loggear
                }
            } else {
                android.util.Log.e("ProgressRepo", "Lessons not available, skipping sync")
                Result.success(Unit) // No romper progreso
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
            if (lessonRepository.ensureLessonsAvailable()) {
                Log.d("PROGRESS_SYNC", "Obteniendo progreso del servidor...")
                val response = apiService.getProgress()
                Log.d("PROGRESS_SYNC", "Respuesta GET /api/progress: code=${response.code()}, isSuccess=${response.isSuccessful}")
                if (response.isSuccessful && response.body() != null) {
                    val remoteProgress = response.body()!!.data
                    Log.d("PROGRESS_SYNC", "Sincronizando ${remoteProgress.size} lecciones desde el servidor")
                    
                    remoteProgress.forEach { dto ->
                        val localId = lessonRepository.getIdBySlug(dto.lessonSlug)?.toString() ?: dto.lessonSlug
                        lessonDao.upsert(
                            CompletedLessonEntity(
                                userId = userId,
                                lessonId = localId,
                                completedAtMillis = dto.completedAt?.let { parseIsoDate(it) } ?: System.currentTimeMillis()
                            )
                        )
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al obtener progreso: ${response.code()}"))
                }
            } else {
                android.util.Log.e("ProgressRepo", "Lessons not available, skipping sync")
                Result.success(Unit) // No romper progreso
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
