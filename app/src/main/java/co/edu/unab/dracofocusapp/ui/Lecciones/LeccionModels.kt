package co.edu.unab.dracofocusapp.ui.Lecciones

data class Leccion(
    val id: String,
    val titulo: String,
    val contexto: String,
    val piezasDisponibles: List<String>,
    val solucionCorrecta: List<String>
)

object LeccionRepository {
    private val lecciones = listOf(
        Leccion(
            id = "1",
            titulo = "Variables de Fuego",
            contexto = "Declara una variable entera llamada 'puntos' con valor 10.",
            piezasDisponibles = listOf("val", "puntos", "=", "10", ": Int", "var"),
            solucionCorrecta = listOf("val", "puntos", ": Int", "=", "10")
        ),
        Leccion(
            id = "2",
            titulo = "Ciclo de Vuelo",
            contexto = "Crea un bucle que se repita 5 veces.",
            piezasDisponibles = listOf("repeat(5)", "{", "}", "for", "while"),
            solucionCorrecta = listOf("repeat(5)", "{", "}")
        ),
        Leccion(
            id = "3",
            titulo = "Condición de Escudo",
            contexto = "Si 'vida' es menor a 20, activa el escudo.",
            piezasDisponibles = listOf("if", "(vida < 20)", "activarEscudo()", "{", "}", "else"),
            solucionCorrecta = listOf("if", "(vida < 20)", "{", "activarEscudo()", "}")
        )
        // Se pueden añadir las 7 restantes siguiendo este patrón
    )

    fun getLeccionesRandom(): List<Leccion> = lecciones.shuffled()
    fun getLeccionById(id: String): Leccion? = lecciones.find { it.id == id }
}
