package co.edu.unab.dracofocusapp.api

import android.net.Uri
import androidx.navigation.NavController
suspend fun enviarCodigoALaIA(
    navController: NavController,
    userId: String,
    leccionId: String,
    codigo: String
) {
    val api = RetrofitClient.instance
    val request = EvaluacionRequest(
        user_id = userId,
        leccion_id = leccionId,
        codigo = codigo
    )

    val result = api.evaluar(request)
    navController.navigate("feedback_screen/${Uri.encode(result.resultado)}")
}

