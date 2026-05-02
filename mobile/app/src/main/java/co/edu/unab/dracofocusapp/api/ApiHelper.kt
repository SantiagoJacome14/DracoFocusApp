package co.edu.unab.dracofocusapp.api

import android.net.Uri
import androidx.navigation.NavController
import co.edu.unab.dracofocusapp.auth.TokenManager
import co.edu.unab.dracofocusapp.data.remote.RetrofitInstance
suspend fun enviarCodigoALaIA(
    navController: NavController,
    userId: String,
    leccionId: String,
    codigo: String,
    tokenManager: TokenManager
) {
    val api = RetrofitInstance.getApiService(tokenManager)
    val request = EvaluacionRequest(
        user_id = userId,
        leccion_id = leccionId,
        codigo = codigo
    )

    val result = api.evaluar(request)
    navController.navigate("feedback_screen/${Uri.encode(result.resultado)}")
}

