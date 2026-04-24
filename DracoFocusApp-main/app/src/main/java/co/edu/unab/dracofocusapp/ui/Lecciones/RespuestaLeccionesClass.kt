package co.edu.unab.dracofocusapp.ui.Lecciones

//Documento que se guarda en Firebase
data class RespuestaLeccionesClass(
    val codigo: String = "",
    val leccionId: String = "decisiones_de_fuego",
    val usuarioId: String = "",
    val fecha: Long = System.currentTimeMillis()
)