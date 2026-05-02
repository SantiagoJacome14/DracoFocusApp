package co.edu.unab.dracofocusapp.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequest): Response<LoginResponse>

    @GET("api/me")
    suspend fun getCurrentUser(): Response<UserDto>

    @GET("api/progress")
    suspend fun getProgress(): Response<ProgressResponse>

    @POST("api/progress/lesson")
    suspend fun sendLessonProgress(@Body request: ProgressRequest): Response<SimpleResponse>
}
