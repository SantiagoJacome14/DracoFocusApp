package co.edu.unab.dracofocusapp.ui.Lecciones

/**
 * Tipos de reto disponibles por lección (el sistema elige uno al entrar).
 */
enum class RetoTipo {
    PUZZLE_OR_CODE_BLOCKS,
    QUIZ_TECH,
    FILL_LINE,
}

/**
 * Contenido mínimo de una lección: puzzle de bloques (ordenar),
 * opcionalmente también datos para quiz técnico o relleno de línea.
 */
data class Leccion(
    val id: String,
    val titulo: String,
    val contexto: String,
    /** Piezas disponibles cuando el reto seleccionado usa bloques. */
    val piezasDisponibles: List<String>,
    val solucionCorrecta: List<String>,
    val opcionesQuiz: List<String> = emptyList(),
    /** Índice de la opción correcta dentro de [opcionesQuiz]. */
    val indiceQuizCorrecto: Int = 0,
    /** Una línea con marcador _____ para fill-in-the-blank. */
    val lineaCodigoHueca: String? = null,
    /** Respuesta esperada (comparación en minúsculas y sin espacios extra). */
    val respuestaRelleno: String? = null,
)

object LeccionRepository {

    private val lecciones = listOf(
        Leccion(
            id = "1",
            titulo = "Variables de Fuego",
            contexto = "Declara una variable entera llamada 'puntos' con valor 10.",
            piezasDisponibles = listOf("val", "puntos", "=", "10", ": Int", "var"),
            solucionCorrecta = listOf("val", "puntos", ": Int", "=", "10"),
            opcionesQuiz = listOf(
                "var puntos: Int = 10",
                "val puntos: Int = 10",
                "val puntos = \"10\"",
                "println(puntos)",
            ),
            indiceQuizCorrecto = 1,
            lineaCodigoHueca = "_____ puntos: Int = 10",
            respuestaRelleno = "val",
        ),
        Leccion(
            id = "2",
            titulo = "Ciclo de Vuelo",
            contexto = "Crea un bucle que se repita 5 veces en Kotlin.",
            piezasDisponibles = listOf("repeat(5)", "{", "}", "for", "while"),
            solucionCorrecta = listOf("repeat(5)", "{", "}"),
            opcionesQuiz = listOf(
                "repeat(5) { }",
                "while (true) { }",
                "for (i in 1..100) break",
                "do { } while (false)",
            ),
            indiceQuizCorrecto = 0,
            lineaCodigoHueca = "repeat(_____) { }",
            respuestaRelleno = "5",
        ),
        Leccion(
            id = "3",
            titulo = "Condición de Escudo",
            contexto = "Si 'vida' es menor a 20, activa el escudo.",
            piezasDisponibles = listOf("if", "(vida < 20)", "activarEscudo()", "{", "}", "else"),
            solucionCorrecta = listOf("if", "(vida < 20)", "{", "activarEscudo()", "}"),
            opcionesQuiz = listOf(
                "if (vida == 20) activarEscudo()",
                "if (vida < 20) { activarEscudo() }",
                "while (vida < 20) { }",
                "else activarEscudo()",
            ),
            indiceQuizCorrecto = 1,
            lineaCodigoHueca = "if (______ < 20) { activarEscudo() }",
            respuestaRelleno = "vida",
        ),
    )

    fun todasLasLecciones(): List<Leccion> = lecciones.shuffled()

    /** Retos ordenados por id estable (fundamentos Draco solo). */
    fun leccionesFundamentosOrden(): List<Leccion> =
        listOfNotNull(getLeccionById("1"), getLeccionById("2"), getLeccionById("3"))

    fun getLeccionById(id: String): Leccion? = lecciones.find { it.id == id }
}
