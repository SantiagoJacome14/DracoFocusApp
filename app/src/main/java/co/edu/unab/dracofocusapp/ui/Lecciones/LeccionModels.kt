package co.edu.unab.dracofocusapp.ui.Lecciones

data class Leccion(
    val id: String,
    val titulo: String,
    val contexto: String,
    val piezasDisponibles: List<String>,
    val solucionCorrecta: List<String>,
    val categoria: String = "Fundamentos"
)

object LeccionRepository {
    private val lecciones = listOf(
        Leccion("1", "Variables de Fuego", "Declara una variable inmutable 'puntos' con valor 10.", listOf("val", "puntos", "=", "10", "var"), listOf("val", "puntos", "=", "10")),
        Leccion("2", "Ciclo de Vuelo", "Crea un bucle que se repita 5 veces.", listOf("repeat(5)", "{", "}", "for", "while"), listOf("repeat(5)", "{", "}")),
        Leccion("3", "Condición de Escudo", "Si 'vida' es menor a 20, activa el escudo.", listOf("if", "(vida < 20)", "{", "activarEscudo()", "}", "else"), listOf("if", "(vida < 20)", "{", "activarEscudo()", "}")),
        Leccion("4", "Lista de Tesoros", "Crea una lista inmutable de nombres.", listOf("listOf(", "\"Oro\"", ",", "\"Gemas\"", ")", "setOf("), listOf("listOf(", "\"Oro\"", ",", "\"Gemas\"", ")")),
        Leccion("5", "Función de Ataque", "Define una función llamada 'atacar'.", listOf("fun", "atacar()", "{", "}", "val", "var"), listOf("fun", "atacar()", "{", "}")),
        Leccion("6", "Null Safety", "Declara un String que puede ser nulo.", listOf("var", "nombre", ": String?", "=", "null", "String"), listOf("var", "nombre", ": String?", "=", "null")),
        Leccion("7", "Clase Dragón", "Crea una clase llamada 'Dragon'.", listOf("class", "Dragon", "{", "}", "object", "interface"), listOf("class", "Dragon", "{", "}")),
        Leccion("8", "Interpolación", "Imprime 'Hola' seguido de la variable 'name'.", listOf("println(", "\"Hola ${name}\"", ")", "print(", "+"), listOf("println(", "\"Hola ${name}\"", ")")),
        Leccion("9", "When de Poder", "Usa 'when' para evaluar la variable 'x'.", listOf("when(x)", "{", "}", "if", "else"), listOf("when(x)", "{", "}")),
        Leccion("10", "Mapa de Cuevas", "Crea un mapa de llaves y valores.", listOf("mapOf(", "1", "to", "\"Cueva\"", ")", "listOf("), listOf("mapOf(", "1", "to", "\"Cueva\"", ")"))
    )

    fun getAll(): List<Leccion> = lecciones
    fun getById(id: String): Leccion? = lecciones.find { it.id == id }
}
