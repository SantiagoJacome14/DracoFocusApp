package co.edu.unab.dracofocusapp.museum

import co.edu.unab.dracofocusapp.R

/**
 * Colección de piezas del museo. Se desbloquean aleatoriamente al completar lecciones.
 * draco_sobre.png se usa solo como imagen del sobre, NO como pieza del catálogo.
 */
object MuseumCatalog {

    data class Piece(
        val catalogId: String,
        val title: String,
        val imageResId: Int,
    )

    val ALL_PIECES: List<Piece> = listOf(
        Piece("m_dracoegipto",      "Draco en Egipto",    R.drawable.dracoegipto),
        Piece("m_dracofiesta",      "Draco en Fiesta",    R.drawable.dracofiesta),
        Piece("m_dracoluna",        "Draco y la Luna",    R.drawable.dracoluna),
        Piece("m_dracomuseoverona", "Draco en Verona",    R.drawable.dracomuseoverona),
        Piece("m_dracoskate",       "Draco Skate",        R.drawable.dracoskate),
        Piece("m_dracoversalles",   "Draco en Versalles", R.drawable.dracoversalles),
    )

    fun lockedStates(unlockedIds: Set<String>): List<Pair<Piece, Boolean>> =
        ALL_PIECES.map { it to (it.catalogId !in unlockedIds) }
}
