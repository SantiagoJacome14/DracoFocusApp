package co.edu.unab.dracofocusapp.museum

import co.edu.unab.dracofocusapp.R

/**
 * Piezas coleccionables: visibles desde el museo incluso antes de obtenerlas (bloqueadas).
 */
object MuseumCatalog {

    data class Piece(
        val catalogId: String,
        val title: String,
        val imageResId: Int,
    )

    val ALL_PIECES: List<Piece> = listOf(
        Piece("m_draco_alba", "Alba Draco", R.drawable.dragon_dracofocus1),
        Piece("m_fuego_codigo", "Fuego binario", R.drawable.dragon_dracofocus1),
        Piece("m_bit_cueva", "Cueva de bits", R.drawable.dragon_dracofocus1),
        Piece("m_loop_infinito", "Loop ascendente", R.drawable.dragon_dracofocus1),
        Piece("m_escudo_logico", "Escudo booleano", R.drawable.dragon_dracofocus1),
        Piece("m_tarea_completada", "Tareas en orden", R.drawable.dragon_dracofocus1),
        Piece("m_draco_gloria", "Gloria del compilador", R.drawable.dragon_dracofocus1),
        Piece("m_overclock", "Overclock cerebral", R.drawable.dragon_dracofocus1),
        Piece("m_stack_ninja", "Ninja del Stack", R.drawable.dragon_dracofocus1),
        Piece("m_final_boss_debug", "Jefe Final: Debug", R.drawable.dragon_dracofocus1),
    )

    fun lockedStates(unlockedIds: Set<String>): List<Pair<Piece, Boolean>> =
        ALL_PIECES.map { it to (it.catalogId !in unlockedIds) }
}
