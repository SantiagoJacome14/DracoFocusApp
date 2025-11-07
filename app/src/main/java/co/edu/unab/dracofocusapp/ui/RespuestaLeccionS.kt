package co.edu.unab.dracofocusapp.ui

//Documento que se guarda en Firebase
data class RespuestaLeccion(
    val codigo: String = "",
    val leccionId: String = "decisiones_de_fuego",
    val usuarioId: String = "",
    val fecha: Long = System.currentTimeMillis()
)