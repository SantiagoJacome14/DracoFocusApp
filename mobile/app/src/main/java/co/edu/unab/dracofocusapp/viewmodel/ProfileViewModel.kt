package co.edu.unab.dracofocusapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.remote.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val apiService: ApiService) : ViewModel() {

    data class UiState(
        val name: String = "",
        val email: String = "",
        val totalXp: Int = 0,
        val currentStreak: Int = 0,
        val dailyGoal: Int = 50,
        val dailyProgressXp: Int = 0,
        val completedLessonsCount: Int = 0,
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
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
