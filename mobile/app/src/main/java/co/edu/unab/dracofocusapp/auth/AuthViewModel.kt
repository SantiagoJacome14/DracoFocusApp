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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

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
            FirebaseAuth.getInstance().signOut()
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
                // PASO 1: Login en Firebase primero
                Log.d("LOGIN", "Iniciando sesión en Firebase para $email")
                var firebaseSuccess = false
                try {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
                    firebaseSuccess = true
                    Log.d("LOGIN", "Firebase Auth exitoso")
                } catch (e: Exception) {
                    Log.e("LOGIN", "Firebase Auth falló: ${e.message}")
                    // Si no está en Firebase, pero quizás sí en Laravel, intentamos crearlo en Firebase
                    try {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
                        firebaseSuccess = true
                        Log.d("LOGIN", "Usuario creado en Firebase durante login")
                    } catch (e2: Exception) {
                        Log.e("LOGIN", "No se pudo sincronizar con Firebase")
                    }
                }

                // PASO 2: Login en Laravel
                Log.d("LOGIN", "Llamando a Laravel /api/login")
                val response = RetrofitInstance.getApiService(tokenManager)
                    .login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        tokenManager.saveAuthData(
                            body.accessToken,
                            body.user.id.toString()
                        )
                        uiState = uiState.copy(isSuccessLogin = true, isLoading = false)
                        onSuccess()
                    } else onError("Respuesta vacía de Laravel")
                } else {
                    val errorJson = response.errorBody()?.string()
                    val serverMessage = try {
                        com.google.gson.JsonParser.parseString(errorJson)
                            .asJsonObject.get("message").asString
                    } catch (e: Exception) {
                        null
                    }

                    Log.e("LOGIN", "Laravel devolvió error ${response.code()}: $serverMessage")
                    onError(serverMessage ?: "Error del servidor (código ${response.code()})")
                }
            } catch (e: Exception) {
                Log.e("LOGIN", "Error en login", e)
                onError("Error de conexión: ${e.message}")
            }
        }
    }

    /**
     * Sincroniza progreso después de login exitoso.
     */
    fun syncAfterLogin(tokenManager: TokenManager) {
        // En modo demo, forzamos un login en Firebase si no hay sesión activa
        viewModelScope.launch {
            if (FirebaseAuth.getInstance().currentUser == null) {
                try {
                    // Intento de login anónimo o silencioso para asegurar que las lecciones funcionen
                    FirebaseAuth.getInstance().signInAnonymously().await()
                    Log.d("MODO_DEMO", "Firebase Auth anónimo activado para el video")
                } catch (e: Exception) {
                    Log.e("MODO_DEMO", "No se pudo activar Firebase anónimo", e)
                }
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

                        // Firebase Auth con Google para Firestore
                        try {
                            val credential = GoogleAuthProvider.getCredential(idToken, null)
                            FirebaseAuth.getInstance().signInWithCredential(credential).await()
                            Log.d("GOOGLE_LOGIN", "Firebase Auth Google exitoso")
                        } catch (e: Exception) {
                            Log.e("GOOGLE_LOGIN", "Error Firebase Auth Google", e)
                        }

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
                        // PASO 1: Registro en Firebase (Indispensable para lecciones grupales y video)
                        Log.d("REGISTRO", "Iniciando registro en Firebase para $email")
                        var firebaseUid: String? = null
                        try {
                            val authResult = FirebaseAuth.getInstance()
                                .createUserWithEmailAndPassword(email, pass).await()
                            firebaseUid = authResult.user?.uid
                            Log.d("REGISTRO", "Firebase Auth exitoso. UID: $firebaseUid")
                            
                            // Crear perfil en Firestore
                            firebaseUid?.let { uid ->
                                val userMap = hashMapOf(
                                    "uid" to uid,
                                    "name" to name,
                                    "email" to email,
                                    "semester" to semester
                                )
                                FirebaseFirestore.getInstance().collection("users")
                                    .document(uid).set(userMap).await()
                                Log.d("REGISTRO", "Perfil Firestore creado")
                            }
                        } catch (e: Exception) {
                            Log.e("REGISTRO", "Firebase falló o usuario ya existe: ${e.message}")
                            // Si el usuario ya existe en Firebase, intentamos loguearlo para obtener el UID
                            try {
                                val authResult = FirebaseAuth.getInstance()
                                    .signInWithEmailAndPassword(email, pass).await()
                                firebaseUid = authResult.user?.uid
                            } catch (e2: Exception) {
                                Log.e("REGISTRO", "No se pudo recuperar sesión Firebase")
                            }
                        }

                        // PASO 2: Registro en Laravel
                        Log.d("REGISTRO", "Llamando a Laravel /api/register")
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
                                    Log.d("REGISTRO", "Laravel exitoso. Token guardado.")

                                    uiState = uiState.copy(
                                        isSuccessLogin = true,
                                        isLoading = false,
                                        errorMessage = null
                                    )
                                    onSuccess()
                                } else onError("Servidor Laravel devolvió datos vacíos")
                            } else {
                                val errorJson = response.errorBody()?.string()
                                val serverMessage = try {
                                    com.google.gson.JsonParser.parseString(errorJson)
                                        .asJsonObject.get("message").asString
                                } catch (e: Exception) {
                                    null
                                }

                                Log.e("REGISTRO", "Laravel devolvió error ${response.code()}: $serverMessage")
                                onError(serverMessage ?: "Error en el servidor: ${response.code()}")
                            }
                        } catch (e: Exception) {
                            Log.e("REGISTRO", "Fallo de conexión Laravel", e)
                            onError("Fallo de conexión: ${e.message}")
                        }
                    } catch (e: Exception) {
                        Log.e("REGISTRO", "Error general crítico", e)
                        onError("Error crítico: ${e.message}")
                    }
                }
            }
        }
    }
}
