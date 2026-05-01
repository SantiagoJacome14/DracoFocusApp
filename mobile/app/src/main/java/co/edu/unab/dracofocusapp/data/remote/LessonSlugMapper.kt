package co.edu.unab.dracofocusapp.data.remote

object LessonSlugMapper {
    private val idToSlug = mapOf(
        "1" to "variables-magicas",
        "2" to "condicionales-del-dragon",
        "3" to "bucles-del-tesoro"
    )

    private val slugToId = idToSlug.entries.associate { it.value to it.key }

    fun getSlugFromId(id: String): String = idToSlug[id] ?: id

    fun getIdFromSlug(slug: String): String = slugToId[slug] ?: slug
}
