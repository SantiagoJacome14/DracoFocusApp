package co.edu.unab.dracofocusapp.data.repo

import co.edu.unab.dracofocusapp.data.local.CompletedLessonEntity
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.ProgressRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class ProgressRepository(
    private val db: DracoDatabase,
    private val apiService: ApiService
) {
    private val lessonDao = db.completedLessonDao()

    /**
     * Observa las lecciones completadas desde Room (Local First)
     */
    fun observeCompletedLessons(): Flow<Set<String>> = 
        lessonDao.observeCompletedIds().map { it.toSet() }

    /**
     * Marca una lección como completada localmente y luego intenta sincronizar con el servidor.
     */
    suspend fun markLessonCompleted(lessonId: String, score: Int = 100): Result<Unit> {
        // 1. Guardar localmente siempre
        lessonDao.upsert(
            CompletedLessonEntity(
                lessonId = lessonId,
                completedAtMillis = System.currentTimeMillis()
            )
        )

        // 2. Intentar enviar al servidor
        return try {
            val response = apiService.sendLessonProgress(ProgressRequest(lessonSlug = lessonId, score = score))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al sincronizar con el servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            // Manejar error de red, pero los datos ya están en Room
            Result.failure(e)
        }
    }

    /**
     * Trae todo el progreso del servidor y actualiza la base de datos local (Sync).
     */
    suspend fun syncProgressFromServer(): Result<Unit> {
        return try {
            val response = apiService.getProgress()
            if (response.isSuccessful && response.body() != null) {
                val remoteProgress = response.body()!!.data
                
                remoteProgress.forEach { dto ->
                    lessonDao.upsert(
                        CompletedLessonEntity(
                            lessonId = dto.lessonId,
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
            // Laravel suele devolver ISO-8601
            OffsetDateTime.parse(isoDate).toInstant().toEpochMilli()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}
