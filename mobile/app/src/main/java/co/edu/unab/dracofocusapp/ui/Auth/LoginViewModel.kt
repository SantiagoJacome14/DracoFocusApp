package co.edu.unab.dracofocusapp.ui.Auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.auth.TokenManager
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.LoginRequest
import co.edu.unab.dracofocusapp.data.repo.LessonProgressRepository
import kotlinx.coroutines.launch

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val repository: LessonProgressRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var state by mutableStateOf(LoginState())

    fun onEmailChange(value: String) { email = value }
    fun onPasswordChange(value: String) { password = value }

    fun login() {
        if (email.isBlank() || password.isBlank()) {
            state = state.copy(error = "Por favor completa todos los campos.")
            return
        }

        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val userId = loginResponse.user.id.toString()
                    
                    // Guardar Token y UserId
                    tokenManager.saveAuthData(loginResponse.accessToken, userId)
                    
                    // Sincronizar progreso inmediatamente tras el login
                    // El repositorio ahora filtrará por este userId
                    repository.syncProgressFromServer(userId)
                    
                    state = state.copy(isLoading = false, isSuccess = true)
                } else {
                    state = state.copy(
                        isLoading = false, 
                        error = "Credenciales incorrectas o error en el servidor."
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false, 
                    error = "Error de conexión: ${e.message}"
                )
            }
        }
    }

    fun onGoogleSignIn(idToken: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                val response = apiService.loginWithGoogle(co.edu.unab.dracofocusapp.data.remote.GoogleAuthRequest(idToken))
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val userId = loginResponse.user.id.toString()

                    tokenManager.saveAuthData(loginResponse.accessToken, userId)
                    repository.syncProgressFromServer(userId)

                    state = state.copy(isLoading = false, isSuccess = true)
                } else {
                    state = state.copy(
                        isLoading = false,
                        error = "Error en la autenticación con Google."
                    )
                }
            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    error = "Error de conexión con el servidor: ${e.message}"
                )
            }
        }
    }

    class Factory(
        private val apiService: ApiService,
        private val tokenManager: TokenManager,
        private val repository: LessonProgressRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(apiService, tokenManager, repository) as T
        }
    }
}
