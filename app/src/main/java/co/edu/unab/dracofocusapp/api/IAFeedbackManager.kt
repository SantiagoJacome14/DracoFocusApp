package co.edu.unab.dracofocusapp.api

import co.edu.unab.dracofocusapp.ui.Lecciones.Leccion
import co.edu.unab.dracofocusapp.ui.Lecciones.RetoTipo
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface IAFeedbackService {
    @POST("v1/feedback")
    suspend fun getFeedback(@Body request: FeedbackRequest): FeedbackResponse
}

data class FeedbackRequest(val prompt: String)
data class FeedbackResponse(val message: String)

class IAFeedbackManager {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://tu-api-ia.com/") // URL base configurada
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(IAFeedbackService::class.java)

    suspend fun generarFeedback(leccion: Leccion, codigoUsuario: String, esCorrecto: Boolean): String {
        val prompt = if (esCorrecto) {
            "Actúa como un tutor amable. El usuario armó correctamente: $codigoUsuario. Felicítalo y dale un dato curioso sobre este tema de programación."
        } else {
            "Actúa como un tutor amable. El usuario armó: $codigoUsuario. La solución era: ${leccion.solucionCorrecta.joinToString(" ")}. No le des la respuesta, dale una pista basada en su error."
        }
        
        return try {
            // En un entorno real, llamaríamos a la API. Aquí simulamos el comportamiento para el MVP.
            // val response = service.getFeedback(FeedbackRequest(prompt))
            // response.message
            if (esCorrecto) "¡Excelente trabajo! Sabías que el término 'val' en Kotlin define una constante inmutable..."
            else "Casi lo logras. Recuerda que en Kotlin el nombre de la variable va antes del tipo. ¡Inténtalo de nuevo!"
        } catch (e: Exception) {
            "Draco está cansado ahora mismo, pero vas por buen camino."
        }
    }

    /**
     * Retroalimentación según tipo de reto (puzzle/quiz/relleno), sin exponer texto exacto cuando falla el quiz/relleno.
     */
    suspend fun generarFeedbackReto(
        leccion: Leccion,
        tipo: RetoTipo,
        entradaUsuarioDesc: String,
        esCorrecto: Boolean,
    ): String {
        val baseTitulo = leccion.titulo
        return try {
            when (tipo) {
                RetoTipo.PUZZLE_OR_CODE_BLOCKS -> generarFeedback(leccion, entradaUsuarioDesc, esCorrecto)
                RetoTipo.QUIZ_TECH ->
                    when {
                        esCorrecto ->
                            "¡Draco aplaude esa elección técnica! En $baseTitulo, esa era la mejor opción. Sigue así."
                        else ->
                            "Casi… Draco revisó tu selección técnica. Piensa cuál sintaxis encaja mejor con el objetivo. " +
                                "¿Estás usando la construcción adecuada para este caso?"
                    }
                RetoTipo.FILL_LINE ->
                    when {
                        esCorrecto ->
                            "¡Tu línea cerró bien el ciclo cognitivo! En $baseTitulo ese hueco ya no es un misterio."
                        else ->
                            "Hmm… ese hueco pide algo más preciso para la condición/objetivo. Relee la consigna y prueba otro símbolo corto antes de la palabra clave."
                    }
            }
        } catch (e: Exception) {
            "Draco necesita café: inténtalo otra vez con calma."
        }
    }
}
