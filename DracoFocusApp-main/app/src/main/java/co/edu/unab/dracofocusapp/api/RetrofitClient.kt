package co.edu.unab.dracofocusapp.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.net.InetAddress
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
// --- Interfaz de comunicación con la API ---
interface DracoApiService {
    @POST("evaluar")
    suspend fun evaluar(@Body request: EvaluacionRequest): EvaluacionResponse
}

// --- Cliente Retrofit dinámico ---
object RetrofitClient {

    private val baseUrl: String by lazy {
        // IP
        // Dirección IPv4. . . . . . . . . . . . . . : 192.168.1.17
        "http://192.168.1.17:8000/"
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val instance: DracoApiService by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DracoApiService::class.java)
    }
}
