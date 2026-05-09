package co.edu.unab.dracofocusapp.data.remote

import com.google.gson.annotations.SerializedName

// Auth DTOs
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val semester: String
)

data class GoogleAuthRequest(
    @SerializedName("id_token") val idToken: String
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    val user: UserDto
)

data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("total_xp") val totalXp: Int = 0,
    @SerializedName("current_streak") val currentStreak: Int = 0,
    @SerializedName("daily_goal") val dailyGoal: Int = 50,
)

// Progress DTOs
data class ProgressResponse(
    @SerializedName("completed_lessons") val completedLessons: List<String>,
    @SerializedName("completed_lesson_ids") val completedLessonIds: List<Int>
)

data class SyncProgressRequest(
    @SerializedName("completed_lessons") val completedLessons: List<String>
)

data class ProgressSyncResponse(
    @SerializedName("completed_lessons") val completedLessons: List<String>,
    @SerializedName("completed_lesson_ids") val completedLessonIds: List<Int>,
    @SerializedName("xp_earned") val xpEarned: Int = 0,
    @SerializedName("total_xp") val totalXp: Int = 0,
)

// User Stats DTO (GET /api/user/stats)
data class CompletedLessonStatsDto(
    val slug: String,
    val title: String?,
    @SerializedName("completed_at") val completedAt: String?,
    @SerializedName("xp_reward") val xpReward: Int = 0,
)

data class BadgeDto(
    val id: String,
    val title: String,
    val description: String,
    val earned: Boolean,
)

data class ChartDataDto(
    val completed: Int = 0,
    val pending: Int = 0,
    @SerializedName("xp_current_level") val xpCurrentLevel: Int = 0,
    @SerializedName("xp_next_level") val xpNextLevel: Int = 200,
)

data class UserStatsDto(
    @SerializedName("total_xp") val totalXp: Int = 0,
    @SerializedName("current_streak") val currentStreak: Int = 0,
    @SerializedName("daily_goal") val dailyGoal: Int = 50,
    val level: Int = 1,
    @SerializedName("completed_lessons_count") val completedLessonsCount: Int = 0,
    @SerializedName("total_lessons_count") val totalLessonsCount: Int = 0,
    @SerializedName("progress_percent") val progressPercent: Int = 0,
    @SerializedName("completed_lessons") val completedLessons: List<CompletedLessonStatsDto> = emptyList(),
    val badges: List<BadgeDto> = emptyList(),
    @SerializedName("chart_data") val chartData: ChartDataDto? = null,
)

// Lesson DTO for dynamic slug mapping
data class LessonDto(
    val id: Int,
    val slug: String,
    val title: String,
    @SerializedName("xp_reward") val xpReward: Int,
    val description: String? = null
)

data class LessonExercisesResponse(
    val lesson: LessonDto,
    val exercises: List<ExerciseDto>
)

data class ExerciseDto(
    val id: Int,
    val type: String,
    val question: String,
    val data: Map<String, Any>?,
    val hint: String?,
    @SerializedName("sort_order") val sortOrder: Int
) {
    /** Safe read from JSONB data field — returns null if key absent or wrong type. */
    fun dataString(key: String): String? = data?.get(key) as? String

    @Suppress("UNCHECKED_CAST")
    fun dataStringList(key: String): List<String>? =
        (data?.get(key) as? List<*>)?.filterIsInstance<String>()

    /** JSON numbers come from Gson as Double; toInt() gives the real integer. */
    fun dataInt(key: String): Int? =
        (data?.get(key) as? Double)?.toInt() ?: (data?.get(key) as? Int)
}
