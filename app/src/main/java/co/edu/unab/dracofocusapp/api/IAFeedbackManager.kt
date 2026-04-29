package co.edu.unab.dracofocusapp.api

import co.edu.unab.dracofocusapp.ui.Lecciones.Leccion
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
}
