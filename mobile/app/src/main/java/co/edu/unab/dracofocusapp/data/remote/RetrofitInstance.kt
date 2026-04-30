package co.edu.unab.dracofocusapp.data.remote

import co.edu.unab.dracofocusapp.auth.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    
    // Cambiar por tu IP local si pruebas con dispositivo físico (ej: 192.168.1.5)
    // Para el emulador de Android, 10.0.2.2 apunta al localhost de la PC
    private const val BASE_URL = "http://10.0.2.2:8000/"

    fun getApiService(tokenManager: TokenManager): ApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
