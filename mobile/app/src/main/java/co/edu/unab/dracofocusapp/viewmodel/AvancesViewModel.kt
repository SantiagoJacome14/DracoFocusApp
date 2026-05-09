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
        data class Success(val stats: UserStatsDto) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val response = apiService.getUserStats()
                if (response.isSuccessful && response.body() != null) {
                    Log.d("AVANCES", "stats OK: ${response.body()}")
                    _state.value = UiState.Success(response.body()!!)
                } else {
                    Log.e("AVANCES", "stats error ${response.code()}")
                    _state.value = UiState.Error("Error del servidor (${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("AVANCES", "stats exception: ${e.message}")
                _state.value = UiState.Error("Sin conexión. Toca reintentar.")
            }
        }
    }

    companion object {
        fun factory(apiService: ApiService): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    AvancesViewModel(apiService) as T
            }
    }
}
