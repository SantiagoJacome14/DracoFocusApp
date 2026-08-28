package co.edu.unab.dracofocusapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.UpdateProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ProfileViewModel(private val apiService: ApiService) : ViewModel() {

    data class UiState(
        val name: String = "",
        val email: String = "",
        val totalXp: Int = 0,
        val currentStreak: Int = 0,
        val dailyGoal: Int = 50,
        val dailyProgressXp: Int = 0,
        val completedLessonsCount: Int = 0,
        val avatarUrl: String? = null,
        val isUploadingAvatar: Boolean = false,
        val bio: String? = null,
        val specialty: String? = null,
        val location: String? = null,
        val githubUrl: String? = null,
        val linkedinUrl: String? = null,
        val websiteUrl: String? = null,
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val isSavingProfile: Boolean = false,
        val error: String? = null,
    ) {
        val level: Int get() = (totalXp / 200) + 1
        val currentLevelXp: Int get() = totalXp % 200
        val hasData: Boolean get() = name.isNotBlank()
    }

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state

    init {
        // Mostrar cache inmediatamente si existe, luego refrescar en background
        val cached = memoryCache
        if (cached != null) {
            _state.value = cached.copy(isLoading = false, isRefreshing = true, error = null)
            Log.d("PROFILE_DEBUG", "Cache encontrado → mostrando datos previos mientras refresca")
        }
        load()
    }

    fun load() {
        val startTime = System.currentTimeMillis()
        viewModelScope.launch {
            val hasCached = memoryCache != null
            if (!hasCached) {
                _state.value = _state.value.copy(isLoading = true, error = null)
            }
            Log.d("PROFILE_DEBUG", "load() iniciado (hasCached=$hasCached)")
            try {
                val userResp = apiService.getCurrentUser()
                val elapsed1 = System.currentTimeMillis() - startTime
                Log.d("PROFILE_DEBUG", "/api/me → code=${userResp.code()} elapsed=${elapsed1}ms")

                val progressResp = apiService.getProgress()
                val elapsed2 = System.currentTimeMillis() - startTime
                Log.d("PROFILE_DEBUG", "/api/progress → code=${progressResp.code()} elapsed=${elapsed2}ms")

                val user = if (userResp.isSuccessful) userResp.body() else null
                val progress = if (progressResp.isSuccessful) progressResp.body() else null

                Log.d("PROFILE_DEBUG", "user: name=${user?.name} xp=${user?.totalXp} streak=${user?.currentStreak} goal=${user?.dailyGoal}")
                Log.d("PROFILE_DEBUG", "progress: completedCount=${progress?.completedLessons?.size}")

                if (!userResp.isSuccessful) {
                    Log.e("PROFILE_DEBUG", "/api/me error: ${userResp.errorBody()?.string()}")
                }

                if (user != null) {
                    val newState = UiState(
                        name = user.name,
                        email = user.email,
                        totalXp = user.totalXp,
                        currentStreak = user.currentStreak,
                        dailyGoal = user.dailyGoal,
                        dailyProgressXp = user.dailyProgressXp,
                        completedLessonsCount = progress?.completedLessons?.size ?: 0,
                        avatarUrl = user.avatar,
                        bio = user.bio,
                        specialty = user.specialty,
                        location = user.location,
                        githubUrl = user.githubUrl,
                        linkedinUrl = user.linkedinUrl,
                        websiteUrl = user.websiteUrl,
                        isLoading = false,
                        isRefreshing = false,
                    )
                    Log.d("PROFILE_DEBUG", "Estado final → level=${newState.level} xp=${newState.totalXp} completadas=${newState.completedLessonsCount}")
                    _state.value = newState
                    memoryCache = newState
                } else {
                    val errorMsg = "HTTP ${userResp.code()}"
                    Log.e("PROFILE_DEBUG", "Error: $errorMsg")
                    if (hasCached) {
                        // Hay cache: no mostrar error, seguir con datos previos
                        _state.value = _state.value.copy(isLoading = false, isRefreshing = false)
                    } else {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = "No se pudo cargar el perfil ($errorMsg)",
                        )
                    }
                }
            } catch (e: Exception) {
                val elapsed = System.currentTimeMillis() - startTime
                Log.e("PROFILE_DEBUG", "Excepción después de ${elapsed}ms: ${e.javaClass.simpleName} — ${e.message}")
                if (memoryCache != null) {
                    _state.value = _state.value.copy(isLoading = false, isRefreshing = false)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = "Sin conexión. Toca reintentar.",
                    )
                }
            }
        }
    }

    /**
     * Guarda los campos editables del perfil (bio, especialidad, ubicación, redes)
     * en Laravel. onResult informa éxito/error para que la UI cierre el diálogo o
     * muestre el mensaje correspondiente.
     */
    fun updateProfile(
        bio: String,
        specialty: String,
        location: String,
        githubUrl: String,
        linkedinUrl: String,
        websiteUrl: String,
        onResult: (success: Boolean, errorMessage: String?) -> Unit,
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSavingProfile = true)
            try {
                val response = apiService.updateProfile(
                    UpdateProfileRequest(
                        bio = bio.ifBlank { null },
                        specialty = specialty.ifBlank { null },
                        location = location.ifBlank { null },
                        githubUrl = githubUrl.ifBlank { null },
                        linkedinUrl = linkedinUrl.ifBlank { null },
                        websiteUrl = websiteUrl.ifBlank { null },
                    )
                )
                if (response.isSuccessful) {
                    val newState = _state.value.copy(
                        bio = bio.ifBlank { null },
                        specialty = specialty.ifBlank { null },
                        location = location.ifBlank { null },
                        githubUrl = githubUrl.ifBlank { null },
                        linkedinUrl = linkedinUrl.ifBlank { null },
                        websiteUrl = websiteUrl.ifBlank { null },
                        isSavingProfile = false,
                    )
                    _state.value = newState
                    memoryCache = newState
                    onResult(true, null)
                } else {
                    _state.value = _state.value.copy(isSavingProfile = false)
                    onResult(false, "Error del servidor (${response.code()})")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSavingProfile = false)
                onResult(false, "Sin conexión: ${e.message}")
            }
        }
    }

    /**
     * Sube una foto nueva de perfil (bytes ya leídos del Uri elegido) y
     * actualiza el avatar con la URL que devuelve el backend (Cloudinary).
     */
    fun uploadAvatar(bytes: ByteArray, fileName: String, mimeType: String, onResult: (success: Boolean, errorMessage: String?) -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isUploadingAvatar = true)
            try {
                val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("photo", fileName, requestBody)
                val response = apiService.uploadAvatar(part)
                if (response.isSuccessful) {
                    val newAvatarUrl = response.body()?.avatar
                    val newState = _state.value.copy(avatarUrl = newAvatarUrl, isUploadingAvatar = false)
                    _state.value = newState
                    memoryCache = newState
                    onResult(true, null)
                } else {
                    _state.value = _state.value.copy(isUploadingAvatar = false)
                    onResult(false, "Error del servidor (${response.code()})")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isUploadingAvatar = false)
                onResult(false, "Sin conexión: ${e.message}")
            }
        }
    }

    companion object {
        @Volatile private var memoryCache: UiState? = null

        fun factory(apiService: ApiService): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ProfileViewModel(apiService) as T
            }
    }
}
