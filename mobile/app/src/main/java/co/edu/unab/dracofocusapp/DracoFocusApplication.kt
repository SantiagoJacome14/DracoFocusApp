package co.edu.unab.dracofocusapp

import android.app.Application
import androidx.room.Room
import co.edu.unab.dracofocusapp.auth.TokenManager
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.remote.ApiService
import co.edu.unab.dracofocusapp.data.remote.RetrofitInstance
import co.edu.unab.dracofocusapp.data.repo.LessonProgressRepository
import co.edu.unab.dracofocusapp.data.repo.LessonRepository
import co.edu.unab.dracofocusapp.domain.rewards.RewardManager

/**
 * DracoFocusApplication: Punto único de configuración para la inyección manual de dependencias.
 */
class DracoFocusApplication : Application() {

    lateinit var database: DracoDatabase
        private set

    lateinit var lessonProgressRepository: LessonProgressRepository
        private set

    lateinit var lessonRepository: LessonRepository
        private set

    lateinit var rewardManager: RewardManager
        private set

    lateinit var tokenManager: TokenManager
        private set
        
    lateinit var apiService: ApiService
        private set

    override fun onCreate() {
        super.onCreate()
        
        // Inicializar base de datos
        database = Room.databaseBuilder(
            applicationContext,
            DracoDatabase::class.java,
            "draco_focus.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        // Inicializar componentes de red y Auth
        tokenManager = TokenManager(applicationContext)
        apiService = RetrofitInstance.getApiService(tokenManager)
        
        // Inicializar Repositorios
        lessonRepository = LessonRepository(database, apiService)
        lessonProgressRepository = LessonProgressRepository(database, apiService, lessonRepository)
        
        // Inicializar Managers
        rewardManager = RewardManager(lessonProgressRepository)
    }
}
