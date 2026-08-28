package co.edu.unab.dracofocusapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.UserStatsDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AvancesViewModel(private val apiService: ApiService) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val stats: UserStatsDto, val isRefreshing: Boolean = false) : UiState()
        data class Error(val message: String) : UiState()
    }

    // Cache en memoria del proceso: si ya cargamos Avances una vez en esta sesión,
    // la siguiente vez que se cree el ViewModel (p. ej. tras volver a la app) mostramos
    // esto de inmediato en vez de un loading en blanco, mientras se refresca en segundo plano.
    private val _state = MutableStateFlow<UiState>(memoryCache?.let { UiState.Success(it) } ?: UiState.Loading)
    val state: StateFlow<UiState> = _state

    fun load() {
        viewModelScope.launch {
            val cachedStats = memoryCache
            // Si ya hay datos (cache o carga previa), no tapamos la pantalla con un
            // loading de pantalla completa: se refresca en silencio.
            if (cachedStats != null) {
                _state.value = UiState.Success(cachedStats, isRefreshing = true)
            } else {
                _state.value = UiState.Loading
            }
            try {
                val response = apiService.getUserStats()
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!
                    Log.d("AVANCES", "stats OK: $stats")
                    memoryCache = stats
                    _state.value = UiState.Success(stats)
                } else {
                    Log.e("AVANCES", "stats error ${response.code()}")
                    if (cachedStats == null) {
                        _state.value = UiState.Error("Error del servidor (${response.code()})")
                    } else {
                        // Falló el refresh pero ya había datos: los dejamos, sin romper la UI.
                        _state.value = UiState.Success(cachedStats)
                    }
                }
            } catch (e: Exception) {
                Log.e("AVANCES", "stats exception: ${e.message}")
                if (cachedStats == null) {
                    _state.value = UiState.Error("Sin conexión. Toca reintentar.")
                } else {
                    _state.value = UiState.Success(cachedStats)
                }
            }
        }
    }

    companion object {
        @Volatile private var memoryCache: UserStatsDto? = null

        fun factory(apiService: ApiService): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AvancesViewModel(apiService) as T
            }
    }
}
