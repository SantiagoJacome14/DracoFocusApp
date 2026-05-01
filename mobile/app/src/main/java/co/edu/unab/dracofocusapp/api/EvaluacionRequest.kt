package co.edu.unab.dracofocusapp.api

data class EvaluacionRequest(
    val user_id: String,
    val leccion_id: String,
    val codigo: String
)

data class EvaluacionResponse(
    val resultado: String
)
