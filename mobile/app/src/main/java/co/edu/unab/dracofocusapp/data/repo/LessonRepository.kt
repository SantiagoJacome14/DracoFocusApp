package co.edu.unab.dracofocusapp.data.repo

import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.local.LessonEntity
import co.edu.unab.dracofocusapp.data.local.LessonExerciseCacheEntity
import co.edu.unab.dracofocusapp.data.local.LessonExerciseProgressEntity
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.ExerciseDto
import co.edu.unab.dracofocusapp.data.remote.LessonDto
import co.edu.unab.dracofocusapp.data.remote.LessonExercisesResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response

class LessonRepository(
    private val db: DracoDatabase,
    private val apiService: ApiService?
) {
    private val lessonDao get() = db.lessonDao()
    private val exerciseProgressDao get() = db.lessonExerciseProgressDao()
    private val exerciseCacheDao get() = db.lessonExerciseCacheDao()
    private val gson = Gson()
    private val jsonMapType = object : TypeToken<Map<String, Any>>() {}.type

    suspend fun fetchLessonsFromApi(): List<LessonDto> {
        return try {
            val response: Response<List<LessonDto>>? = apiService?.getLessons()
            if (response != null && response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                android.util.Log.e("LessonRepository", "Error fetching lessons: ${response?.code()} - ${response?.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            android.util.Log.e("LessonRepository", "Exception fetching lessons: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun saveLessonsInRoom(lessons: List<LessonDto>) {
        val entities = lessons.map { dto ->
            LessonEntity(
                id = dto.id,
                slug = dto.slug,
                title = dto.title,
                xpReward = dto.xpReward
            )
        }
        lessonDao.insertLessons(entities)
    }

    suspend fun getSlugById(lessonId: Int): String? {
        return lessonDao.getSlugById(lessonId)
    }

    suspend fun getIdBySlug(slug: String): Int? {
        return lessonDao.getIdBySlug(slug)
    }

    suspend fun clearLessons() {
        lessonDao.clearAll()
    }

    suspend fun ensureLessonsAvailable(): Boolean {
        val existing = lessonDao.getAllLessons()
        if (existing.isEmpty()) {
            val fetched = fetchLessonsFromApi()
            if (fetched.isNotEmpty()) {
                saveLessonsInRoom(fetched)
                return true
            }
            return false
        }
        return true
    }

    suspend fun saveExerciseProgress(userId: String, lessonSlug: String, index: Int) {
        exerciseProgressDao.upsert(LessonExerciseProgressEntity(userId, lessonSlug, index))
        android.util.Log.d("EXERCISE_PROGRESS", "Saved: userId=$userId slug=$lessonSlug index=$index")
    }

    suspend fun loadExerciseProgress(userId: String, lessonSlug: String): Int {
        val idx = exerciseProgressDao.getIndex(userId, lessonSlug) ?: 0
        android.util.Log.d("EXERCISE_PROGRESS", "Loaded: userId=$userId slug=$lessonSlug index=$idx")
        return idx
    }

    suspend fun clearExerciseProgress(userId: String, lessonSlug: String) {
        exerciseProgressDao.upsert(LessonExerciseProgressEntity(userId, lessonSlug, 0))
    }

    suspend fun fetchExercisesForLesson(slug: String): co.edu.unab.dracofocusapp.data.remote.LessonExercisesResponse? {
        android.util.Log.d("LESSON_DEBUG", "Fetching exercises for slug=$slug")
        return try {
            val response = apiService?.getLessonExercises(slug)
            if (response != null && response.isSuccessful) {
                android.util.Log.d("LESSON_DEBUG", "Exercises response code=${response.code()} slug=$slug")
                response.body()
            } else {
                android.util.Log.e("LESSON_DEBUG", "Error fetching exercises for slug=$slug response code=${response?.code()}")
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("LESSON_DEBUG", "Error fetching exercises for slug=$slug", e)
            null
        }
    }

    /**
     * Lee los ejercicios de una lección SOLO desde Room (sin red). Devuelve null si nunca
     * se descargó/cacheó esa lección. Permite abrirla al instante en visitas posteriores.
     */
    suspend fun getCachedExercises(slug: String): LessonExercisesResponse? {
        val cachedExercises = exerciseCacheDao.getForLesson(slug)
        if (cachedExercises.isEmpty()) return null
        val lessonEntity = lessonDao.getBySlug(slug) ?: return null

        return LessonExercisesResponse(
            lesson = LessonDto(
                id = lessonEntity.id,
                slug = lessonEntity.slug,
                title = lessonEntity.title,
                xpReward = lessonEntity.xpReward,
            ),
            exercises = cachedExercises.map { it.toDto() },
        )
    }

    /**
     * Pide los ejercicios al backend y, si responde bien, los guarda en Room para la
     * próxima vez. Es la misma llamada de red de siempre (fetchExercisesForLesson) más
     * el guardado en caché.
     */
    suspend fun fetchAndCacheExercises(slug: String): LessonExercisesResponse? {
        val response = fetchExercisesForLesson(slug)
        if (response != null) {
            saveLessonsInRoom(listOf(response.lesson))
            exerciseCacheDao.clearForLesson(slug)
            exerciseCacheDao.insertAll(response.exercises.map { it.toEntity(slug) })
        }
        return response
    }

    private fun LessonExerciseCacheEntity.toDto(): ExerciseDto = ExerciseDto(
        id = exerciseId,
        type = type,
        question = question,
        data = dataJson?.let { gson.fromJson<Map<String, Any>>(it, jsonMapType) },
        hint = hint,
        sortOrder = sortOrder,
    )

    private fun ExerciseDto.toEntity(lessonSlug: String): LessonExerciseCacheEntity = LessonExerciseCacheEntity(
        lessonSlug = lessonSlug,
        exerciseId = id,
        type = type,
        question = question,
        dataJson = data?.let { gson.toJson(it) },
        hint = hint,
        sortOrder = sortOrder,
    )
}
