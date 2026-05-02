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
    val email: String
)

// Progress DTOs
data class ProgressResponse(
    val status: String,
    val data: List<UserProgressDto>
)

data class UserProgressDto(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("lesson_id") val lessonId: String,
    val score: Int,
    @SerializedName("completed_at") val completedAt: String
)

data class ProgressRequest(
    @SerializedName("lesson_slug") val lessonSlug: String,
    val score: Int? = 100
)

data class SimpleResponse(
    val status: String,
    val message: String
)

// Lesson DTO for dynamic slug mapping
data class LessonDto(
    val id: Int,
    val slug: String,
    val title: String,
    @SerializedName("xp_reward") val xpReward: Int
)
