package co.edu.unab.dracofocusapp.auth

// ESTADO
// Esta es la DataClass, que es la unica fuente donde contiene todas las variables necesarias para
// pintar la UI y mostrar mensajes donde especifica cuales son los tipos de datos, string, number etc

data class AuthUiState(
    // Campos del formulario de registro
    val signUpName: String = "", // Guarda el texto
    val signUpEmail: String = "", // Guarda el texto
    val signUpSemester: String = "", // Guarda el texto
    val signUpPassword: String = "", // Guarda el texto
    val signUpConfirmPassword: String = "", // Guarda el texto
    val isSignUp: Boolean = false,  // Indica si el usuario está en el flujo de registro
    val errorMessage: String? = null, // Mensaje de error si el registro le da falla como por ejemploe l correo ya en uso
    val isSuccessLogin: Boolean = false, // Muestra si el login fue exitoso

    // Campos del formulario de login
    val loginEmail: String = "", // Guarda el texto
    val loginPassword: String = "", // Guarda el texto

    // Tipos de Errores por las validaciones

    // Errores de validación de registro
    // Son los que van debajo de cada cuadro
    val signUpNameError: String? = null, // Mensaje error
    val signUpEmailError: String? = null, // Mensaje error
    val signUpSemesterError: String? = null, // Mensaje error
    val signUpPasswordError: String? = null, // Mensaje error
    val signUpConfirmPasswordError: String? = null, // Mensaje error

    // Errores de validación de login
    val loginEmailError: String? = null, // Mensaje error
    val loginPasswordError: String? = null, // Mensaje error

    // Estado de la UI
    val isLoading: Boolean = false, // Para mostrar que esta cargando
    val generalError: String? = null // En este son cuando son errores de Firebase como cuando la red falla o asi
)

//  EVENTOS DE NAVEGACIÓN
// Se utiliza sealed class para un conjunto de eventos,
// Estos eventos son las acciones que la pantalla ejecuta despues de que el ViewModel lo lea
sealed class AuthUiEvent {
    object NavigateToMain : AuthUiEvent() // Evento de navegacion para la pantalla principal

}

