package co.edu.unab.dracofocusapp.data.remote

import co.edu.unab.dracofocusapp.api.EvaluacionRequest
import co.edu.unab.dracofocusapp.api.EvaluacionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequest): Response<LoginResponse>

    @GET("api/me")
    suspend fun getCurrentUser(): Response<UserDto>

    @GET("api/progress")
    suspend fun getProgress(): Response<ProgressResponse>

    @POST("api/progress/sync")
    suspend fun syncProgress(@Body request: SyncProgressRequest): Response<ProgressSyncResponse>

    @GET("api/user/stats")
    suspend fun getUserStats(): Response<UserStatsDto>

    @GET("api/lessons")
    suspend fun getLessons(): Response<List<LessonDto>>

    @GET("api/lessons/{slug}/exercises")
    suspend fun getLessonExercises(@Path("slug") slug: String): Response<LessonExercisesResponse>

    @POST("api/evaluar")
    suspend fun evaluar(@Body request: EvaluacionRequest): EvaluacionResponse

    @GET("api/museum/rewards")
    suspend fun getMuseumRewards(): Response<MuseumRewardsResponse>

    @POST("api/museum/rewards/claim")
    suspend fun claimMuseumReward(@Body request: MuseumClaimRequest): Response<MuseumClaimResponse>

    // ─── Group Mode endpoints ─────────────────────────────────────────────────
    @POST("api/groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): Response<GroupSessionResponse>

    @POST("api/groups/join")
    suspend fun joinGroup(@Body request: JoinGroupRequest): Response<GroupSessionResponse>

    @GET("api/groups/{code}")
    suspend fun getGroup(@Path("code") code: String): Response<GroupSessionResponse>

    @GET("api/groups/{code}/members")
    suspend fun getGroupMembers(@Path("code") code: String): Response<List<GroupMemberDto>>
}
