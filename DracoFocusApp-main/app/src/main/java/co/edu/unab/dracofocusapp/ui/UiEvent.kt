package co.edu.unab.dracofocusapp.ui

sealed class UiEvent {
    data class ShowSnackbar(
        val message: String,
        val action: String? = null
    ) : UiEvent()
    // Aquí podemos agregar más eventos como Navigate, etc.
}
