package co.edu.unab.dracofocusapp.data.repo

import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.local.LessonEntity
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.LessonDto

class LessonRepository(
    private val db: DracoDatabase,
    private val apiService: ApiService?
) {
    private val lessonDao get() = db.lessonDao()

    suspend fun fetchLessonsFromApi(): List<LessonDto> {
        return try {
            apiService?.getLessons() ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
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
}
