package co.edu.unab.dracofocusapp.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.auth.auth

// Esta clase es para la lógica de la autenticación del login y el registro
// Es la que hereda de ViewModel para que los datos sirvan despues de los cambiosde la pantalla como por ejemplo girar el telefono
class AuthViewModel : ViewModel() {
    //  necesario para llamar a signOut y a los metodos de login y registro
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore
    // Se guarda el estado de la UI de AuthUiState
    var uiState by mutableStateOf(AuthUiState()) // mutableStateOf y by para que sepa automáticamente cuándo debe rehacerse si cambia el estado

    // Errores
    // Error General de la Ui
    fun onError(message: String?) {
        uiState = uiState.copy(
            isLoading = false,  // Detiene la carga y pone el mensaje de abajo
            errorMessage = message ?: "Ocurrió un error inesperado."
        )
    }
    // Oculta el mensaje de error
    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
    // Para cambiar entre Login y Register
    fun onToggleAuthMode() {
        uiState = uiState.copy(
            isSignUp = !uiState.isSignUp, // Esto lo que hace es cambiar el valor booleano por el contrario que tenia, si era falso lo pasa a verdadero y asi
            errorMessage = null // Quitar el error cuando se cambia entre login y register
        )
    }
    // Deja todos los campos que hay que rellenar, vacios
    fun clearForm() {
        uiState = uiState.copy(
            signUpName = "",
            signUpEmail = "",
            signUpSemester = "",
            signUpPassword = "",
            signUpConfirmPassword = "",
            loginEmail = "",
            loginPassword = "",
            errorMessage = null // Quitar el error cuando se cambia entre login y register
        )
    }
    // Cerrar sesion
    fun signOut() {
        // Cierra la sesion en Firebase auth
        auth.signOut()

        // Reinicia las banderas de estado para forzar la vuelta al login
        uiState = uiState.copy(
            isSuccessLogin = false,
            isLoading = false,
            errorMessage = null
        )
        clearForm() // limpia cualquier texto en login o register
    }

    // LOGIN

    // Se utiliza cuando el user escribe en el campo de correo en Login
    fun onLoginEmailChanged(value: String) { // Actualiza loginemail en el estado
        uiState = uiState.copy(loginEmail = value.trim()) // El trim es para borrar espacios al principio y al final
    }
    // Se utiliza cuando el user escribe en el campo de password en Login
    fun onLoginPasswordChanged(value: String) { // Actualiza loginPassword en el estado
        uiState = uiState.copy(loginPassword = value)
    }
    // Cuando el user le da click al boton de Iniciar Sesion
    fun onLoginClicked() {
        if (uiState.loginEmail.isBlank() || uiState.loginPassword.isBlank()) { // Valida si los campos estan vacios
            onError("Por favor, completa todos los campos.") // Y si estan vacios sale este mensaje
            return
        }
        // Despues si pasa, empieza a cargar el estado

        uiState = uiState.copy(isLoading = true, errorMessage = null) // Esto es un borrador para despues cambiarlo en FireBase
        onLoginSuccess() //Simula
    }
// Llama a Firebase y confirma que el login fue exitoso
    fun onLoginSuccess() {
        uiState = uiState.copy(
            isSuccessLogin = true, // true para que navegue
            isLoading = false,
            errorMessage = null
        )
    }

    fun onSuccess() {
        uiState = uiState.copy(
            isSuccessLogin = true,
            isLoading = false,
            errorMessage = null
        )
        clearForm() // Limpiamos el formulario después del éxito
    }


    // Registro
    // Se llama cuando se escribe el nombre
    fun onSignUpNameChanged(value: String) {
        val filtered = value.filter { it.isLetter() || it.isWhitespace() } // Solo permitir letras y espacios
        uiState = uiState.copy(signUpName = filtered)
    }
    // Se llama cuando se escribe el correo
    fun onSignUpEmailChanged(value: String) {
        uiState = uiState.copy(signUpEmail = value.trim()) // El trim es para borrar espacios al principio y al final
    }
    // Se llama cuando se escribe el semestre
    fun onSignUpSemesterChanged(value: String) {
        val filtered = value.filter { it.isDigit() }.take(2) // Solo permitir digitos y toma los primeros 2
        uiState = uiState.copy(signUpSemester = filtered)
    }
    // Se llama cuando se escribe la password del registro
    fun onSignUpPasswordChanged(value: String) {
        uiState = uiState.copy(signUpPassword = value)
    }
    // Se llama cuando se escribe la confirmacion de password
    fun onSignUpConfirmPasswordChanged(value: String) {
        uiState = uiState.copy(signUpConfirmPassword = value)
    }

    // Se llama cuando se le da click a registrarse
    fun onSignUpClicked() {
        // Extraer todos los valores para simplificar la validacion
        val name = uiState.signUpName
        val email = uiState.signUpEmail
        val semester = uiState.signUpSemester
        val pass = uiState.signUpPassword
        val confirm = uiState.signUpConfirmPassword

        // When para que revise todas las condiciones, una por una
        when {
            name.isBlank() || email.isBlank() || semester.isBlank() || pass.isBlank() || confirm.isBlank() -> { //Cuando hay campos vacios
                onError("Completa todos los campos para continuar.")
            }
            pass.length < 6 -> { // Cuando la password es muy cortta
                onError("La contraseña debe tener al menos 6 caracteres.")
            }
            pass != confirm -> { // Cuando las contrasenas no coinciden
                onError("Las contraseñas no coinciden.")
            }
            else -> { // Si pasa todas las validacionrs
                uiState = uiState.copy(isLoading = true, errorMessage = null) // Cambia al estado de carga para empezar con firebase
                onLoginSuccess() //Por el momento es una simulacion
            }
        }
    }
}

