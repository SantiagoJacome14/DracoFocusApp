package co.edu.unab.dracofocusapp.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.remote.GoogleAuthRequest
import co.edu.unab.dracofocusapp.data.remote.LoginRequest
import co.edu.unab.dracofocusapp.data.remote.RetrofitInstance
import kotlinx.coroutines.launch
import android.util.Log
import co.edu.unab.dracofocusapp.data.remote.RegisterRequest

class AuthViewModel : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())

    fun onError(message: String?) {
        uiState = uiState.copy(
            isLoading = false,
            errorMessage = message ?: "Ocurrió un error inesperado."
        )
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun onToggleAuthMode() {
        uiState = uiState.copy(
            isSignUp = !uiState.isSignUp,
            errorMessage = null
        )
    }

    fun clearForm() {
        uiState = uiState.copy(
            signUpName = "",
            signUpEmail = "",
            signUpSemester = "",
            signUpPassword = "",
            signUpConfirmPassword = "",
            loginEmail = "",
            loginPassword = "",
            errorMessage = null
        )
    }

    fun signOut(tokenManager: TokenManager? = null) {
        viewModelScope.launch {
            tokenManager?.clearAuthData()
        }
        uiState = uiState.copy(
            isSuccessLogin = false,
            isLoading = false,
            errorMessage = null
        )
        clearForm()
    }

    // LOGIN

    fun onLoginEmailChanged(value: String) {
        uiState = uiState.copy(loginEmail = value.trim())
    }

    fun onLoginPasswordChanged(value: String) {
        uiState = uiState.copy(loginPassword = value)
    }

    fun onSuccess() {
        uiState = uiState.copy(
            isSuccessLogin = true,
            isLoading = false,
            errorMessage = null
        )
        clearForm()
    }

    /**
     * Login con email/password vía Laravel.
     * Guarda token + userId en TokenManager y llama syncProgressFromServer.
     */
    fun loginWithEmail(
        email: String,
        password: String,
        tokenManager: TokenManager,
        onSuccess: () -> Unit
    ) {
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                Log.d("EMAIL_LOGIN", "Llamando a Laravel /api/login")
                val response = RetrofitInstance.getApiService(tokenManager)
                    .login(LoginRequest(email, password))

                Log.d("EMAIL_LOGIN", "Respuesta Laravel code: ${response.code()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        tokenManager.saveAuthData(
                            body.accessToken,
                            body.user.id.toString()
                        )
                        Log.d("EMAIL_LOGIN", "Token y userId guardados. userId=${body.user.id}")

                        uiState = uiState.copy(
                            isSuccessLogin = true,
                            isLoading = false
                        )
                        onSuccess()
                    } else {
                        onError("Respuesta vacía del servidor")
                    }
                } else {
                    onError("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                Log.e("EMAIL_LOGIN", "Error en login email", e)
                onError("Error de conexión: ${e.message}")
            }
        }
    }

    /**
     * Sincroniza progreso después de login exitoso.
     */
    fun syncAfterLogin(tokenManager: TokenManager) {
        viewModelScope.launch {
            val userId = tokenManager.userId
            if (userId != null) {
                Log.d("PROGRESS_SYNC", "syncAfterLogin para userId=$userId")
                // Se delega al repository a través del Application o ViewModel
            }
        }
    }

    // GOOGLE SIGN IN
    fun onGoogleSignIn(idToken: String, tokenManager: TokenManager, onSuccess: () -> Unit) {
        Log.d("GOOGLE_LOGIN", "Entró a ViewModel")
        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                Log.d("GOOGLE_LOGIN", "Llamando a Laravel /api/auth/google")
                val response = RetrofitInstance.getApiService(tokenManager)
                    .loginWithGoogle(GoogleAuthRequest(idToken))

                Log.d("GOOGLE_LOGIN", "Respuesta Laravel code: ${response.code()}")

                if (response.isSuccessful) {
                    Log.d("GOOGLE_LOGIN", "Login exitoso, guardando token")
                    val body = response.body()

                    if (body != null) {
                        tokenManager.saveAuthData(
                            body.accessToken,
                            body.user.id.toString()
                        )

                        uiState = uiState.copy(
                            isSuccessLogin = true,
                            isLoading = false
                        )

                        onSuccess()
                    } else {
                        onError("Respuesta vacía del servidor")
                    }
                } else {
                    onError("Error HTTP: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("GOOGLE_LOGIN", "Error en login Google", e)
                onError("Error de conexión con el servidor: ${e.message}")
            }
        }
    }

    // Registro
    fun onSignUpNameChanged(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() }
        uiState = uiState.copy(signUpName = filtered)
    }

    fun onSignUpEmailChanged(value: String) {
        uiState = uiState.copy(signUpEmail = value.trim())
    }

    fun onSignUpSemesterChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(2)
        uiState = uiState.copy(signUpSemester = filtered)
    }

    fun onSignUpPasswordChanged(value: String) {
        uiState = uiState.copy(signUpPassword = value)
    }

    fun onSignUpConfirmPasswordChanged(value: String) {
        uiState = uiState.copy(signUpConfirmPassword = value)
    }

    /**
     * Registro vía Laravel backend.
     * Guarda token + userId en TokenManager.
     */
    fun registerWithEmail(
        tokenManager: TokenManager,
        onSuccess: () -> Unit
    ) {
        val name = uiState.signUpName
        val email = uiState.signUpEmail
        val semester = uiState.signUpSemester
        val pass = uiState.signUpPassword
        val confirm = uiState.signUpConfirmPassword

        when {
            name.isBlank() || email.isBlank() || semester.isBlank() || pass.isBlank() || confirm.isBlank() -> {
                onError("Completa todos los campos.")
            }
            pass.length < 6 -> onError("Mínimo 6 caracteres.")
            pass != confirm -> onError("Las contraseñas no coinciden.")
            else -> {
                uiState = uiState.copy(isLoading = true, errorMessage = null)

                viewModelScope.launch {
                    try {
                        val response = RetrofitInstance.getApiService(tokenManager)
                            .register(
                                RegisterRequest(
                                    name = name,
                                    email = email,
                                    password = pass,
                                    password_confirmation = confirm,
                                    semester = semester
                                )
                            )

                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null) {
                                tokenManager.saveAuthData(
                                    body.accessToken,
                                    body.user.id.toString()
                                )

                                uiState = uiState.copy(
                                    isSuccessLogin = true,
                                    isLoading = false,
                                    errorMessage = null
                                )
                                onSuccess()
                            } else onError("Respuesta vacía")
                        } else {
                            onError("Error registro: ${response.code()}")
                        }
                    } catch (e: Exception) {
                        onError("Conexión: ${e.message}")
                    }
                }
            }
        }
    }
}
