package co.edu.unab.dracofocusapp.data.remote

import co.edu.unab.dracofocusapp.auth.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    
<<<<<<< HEAD
    private const val BASE_URL = "https://dracofocusapp.onrender.com/"
=======
    // PARA TELÉFONO FÍSICO: Cambiar por la IP local de tu PC
    // Ejecuta ipconfig en CMD y usa la IPv4 de tu WiFi/Ethernet
    // Ejemplo: private const val BASE_URL = "http://192.168.1.5:8001/"
    // NO usar 10.0.2.2 (solo emulador) ni 127.0.0.1 (es el propio teléfono)
    private const val BASE_URL = "http://10.0.2.2:8001/api/"
>>>>>>> origin/main

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
