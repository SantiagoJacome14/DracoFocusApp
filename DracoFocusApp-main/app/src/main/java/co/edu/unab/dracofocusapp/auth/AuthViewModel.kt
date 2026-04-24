package co.edu.unab.dracofocusapp.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unab.dracofocusapp.data.repository.AuthRepository
import co.edu.unab.dracofocusapp.ui.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(AuthUiState())
        private set

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun onError(message: String?) {
        val errorMsg = message ?: "Ocurrió un error inesperado."
        uiState = uiState.copy(isLoading = false, errorMessage = errorMsg)
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowSnackbar(errorMsg))
        }
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

    fun onLoginEmailChanged(value: String) {
        uiState = uiState.copy(loginEmail = value.trim())
    }

    fun onLoginPasswordChanged(value: String) {
        uiState = uiState.copy(loginPassword = value)
    }

    fun onLoginClicked() {
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
                        _uiEvent.emit(UiEvent.ShowSnackbar("¡Bienvenido de nuevo!"))
                    },
                    onFailure = { error ->
                        onError(error.message)
                    }
                )
            }
        }
    }

    // Registro similar...
    fun onSignUpClicked() {
        val name = uiState.signUpName
        val email = uiState.signUpEmail
        val pass = uiState.signUpPassword
        val confirm = uiState.signUpConfirmPassword

        if (name.isBlank() || email.isBlank() || pass.isBlank() || confirm.isBlank()) {
            onError("Completa todos los campos.")
            return
        }

        if (pass != confirm) {
            onError("Las contraseñas no coinciden.")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            authRepository.register(email, pass, name).collect { result ->
                result.fold(
                    onSuccess = {
                        uiState = uiState.copy(isLoading = false, isSuccessLogin = true)
                        _uiEvent.emit(UiEvent.ShowSnackbar("¡Cuenta creada con éxito!"))
                    },
                    onFailure = { error ->
                        onError(error.message)
                    }
                )
            }
        }
    }
}
