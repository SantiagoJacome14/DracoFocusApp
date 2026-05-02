package co.edu.unab.dracofocusapp.ui.Auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.auth.TokenManager
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.GoogleAuthRequest
import co.edu.unab.dracofocusapp.data.remote.LoginRequest
import co.edu.unab.dracofocusapp.data.repo.LessonProgressRepository
import co.edu.unab.dracofocusapp.data.repo.LessonRepository
import kotlinx.coroutines.launch

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(
    private val apiService: ApiService,
    private val tokenManager: TokenManager,
    private val repository: LessonProgressRepository,
    private val lessonRepository: LessonRepository? = null
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

                    // Fetch lecciones dinámicas desde el backend
                    lessonRepository?.fetchLessonsFromApi()?.let { lessons ->
                        lessonRepository.saveLessonsInRoom(lessons)
                    }

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
        val tokenPreview = if (idToken.length > 20) idToken.take(20) + "..." else idToken
        Log.d("GOOGLE_LOGIN", "[VM-1] onGoogleSignIn iniciado, token=$tokenPreview")
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            try {
                Log.d("GOOGLE_LOGIN", "[VM-2] Llamando a apiService.loginWithGoogle()")
                val response = apiService.loginWithGoogle(GoogleAuthRequest(idToken))
                Log.d("GOOGLE_LOGIN", "[VM-3] Response recibido: isSuccessful=${response.isSuccessful}, code=${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    val userId = loginResponse.user.id.toString()
                    Log.d("GOOGLE_LOGIN", "[VM-4] Login exitoso: userId=$userId, name=${loginResponse.user.name}")

                    tokenManager.saveAuthData(loginResponse.accessToken, userId)
                    Log.d("GOOGLE_LOGIN", "[VM-5] Token guardado en DataStore, obteniendo lecciones dinámicas")
                    lessonRepository?.fetchLessonsFromApi()?.let { lessons ->
                        lessonRepository.saveLessonsInRoom(lessons)
                    }
                    Log.d("GOOGLE_LOGIN", "[VM-5] Token guardado en DataStore, sincronizando progreso")
                    repository.syncProgressFromServer(userId)

                    state = state.copy(isLoading = false, isSuccess = true)
                    Log.d("GOOGLE_LOGIN", "[VM-6] state.isSuccess = true, navegación pendiente")
                } else {
                    val errorBody = response.errorBody()?.string() ?: "(sin body)"
                    Log.e("GOOGLE_LOGIN", "[VM-3] Response FALLIDO: code=${response.code()}, body=$errorBody")
                    state = state.copy(
                        isLoading = false,
                        error = "Error en la autenticación con Google."
                    )
                }
            } catch (e: Exception) {
                Log.e("GOOGLE_LOGIN", "[VM] Excepción: ${e::class.simpleName} - ${e.message}", e)
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
        private val repository: LessonProgressRepository,
        private val lessonRepository: LessonRepository? = null
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(apiService, tokenManager, repository, lessonRepository) as T
        }
    }
}
