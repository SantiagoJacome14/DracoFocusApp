package co.edu.unab.dracofocusapp.data.repo

import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.local.LessonEntity
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.LessonDto
import retrofit2.Response

class LessonRepository(
    private val db: DracoDatabase,
    private val apiService: ApiService?
) {
    private val lessonDao get() = db.lessonDao()

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
}
