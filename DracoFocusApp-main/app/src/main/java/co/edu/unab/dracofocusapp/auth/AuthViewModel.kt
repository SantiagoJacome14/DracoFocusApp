package co.edu.unab.dracofocusapp.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

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

    fun signOut() {
        authRepository.logout()
        uiState = uiState.copy(
            isSuccessLogin = false,
            isLoading = false,
            errorMessage = null
        )
        clearForm()
    }

    fun onLoginEmailChanged(value: String) {
        uiState = uiState.copy(loginEmail = value.trim())
    }

    fun onLoginPasswordChanged(value: String) {
        uiState = uiState.copy(loginPassword = value)
    }

    fun onLoginClicked(onSuccess: () -> Unit) {
        if (uiState.loginEmail.isBlank() || uiState.loginPassword.isBlank()) {
            onError("Por favor, completa todos los campos.")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            authRepository.login(uiState.loginEmail, uiState.loginPassword).collect { result ->
                result.fold(
                    onSuccess = {
                        uiState = uiState.copy(isLoading = false, isSuccessLogin = true)
                        onSuccess()
                    },
                    onFailure = { error ->
                        onError(error.message)
                    }
                )
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

    fun onSignUpClicked(onSuccess: () -> Unit) {
        val name = uiState.signUpName
        val email = uiState.signUpEmail
        val semester = uiState.signUpSemester
        val pass = uiState.signUpPassword
        val confirm = uiState.signUpConfirmPassword

        when {
            name.isBlank() || email.isBlank() || semester.isBlank() || pass.isBlank() || confirm.isBlank() -> {
                onError("Completa todos los campos para continuar.")
            }
            pass.length < 6 -> {
                onError("La contraseña debe tener al menos 6 caracteres.")
            }
            pass != confirm -> {
                onError("Las contraseñas no coinciden.")
            }
            else -> {
                uiState = uiState.copy(isLoading = true, errorMessage = null)
                viewModelScope.launch {
                    authRepository.register(email, pass, name).collect { result ->
                        result.fold(
                            onSuccess = {
                                uiState = uiState.copy(isLoading = false, isSuccessLogin = true)
                                onSuccess()
                            },
                            onFailure = { error ->
                                onError(error.message)
                            }
                        )
                    }
                }
            }
        }
    }
}
