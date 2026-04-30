package co.edu.unab.dracofocusapp

import android.app.Application
import androidx.room.Room
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.repo.LessonProgressRepository
import co.edu.unab.dracofocusapp.domain.rewards.RewardManager

/**
 * Punto único para base de datos local y RewardManager (sobres + fichas del museo).
 */
class DracoFocusApplication : Application() {

    lateinit var database: DracoDatabase
        private set

    lateinit var lessonProgressRepository: LessonProgressRepository
        private set

    lateinit var rewardManager: RewardManager
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            DracoDatabase::class.java,
            "draco_focus.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        lessonProgressRepository = LessonProgressRepository(database)
        rewardManager = RewardManager(lessonProgressRepository)
    }
}
