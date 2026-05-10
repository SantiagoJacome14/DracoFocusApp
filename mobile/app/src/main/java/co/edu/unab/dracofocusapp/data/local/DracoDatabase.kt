package co.edu.unab.dracofocusapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import co.edu.unab.dracofocusapp.data.local.dao.CompletedLessonDao
import co.edu.unab.dracofocusapp.data.local.dao.LessonDao
import co.edu.unab.dracofocusapp.data.local.dao.LessonExerciseProgressDao
import co.edu.unab.dracofocusapp.data.local.dao.LessonRewardClaimsDao
import co.edu.unab.dracofocusapp.data.local.dao.MuseumUnlockDao
import co.edu.unab.dracofocusapp.data.local.dao.RewardFlagsDao

@Database(
    exportSchema = false,
    entities = [
        CompletedLessonEntity::class,
        RewardFlagsEntity::class,
        MuseumUnlockEntity::class,
        LessonEntity::class,
        LessonExerciseProgressEntity::class,
        LessonRewardClaimEntity::class,
    ],
    version = 5  // bumped: added lesson_reward_claims table
)
abstract class DracoDatabase : RoomDatabase() {

    abstract fun completedLessonDao(): CompletedLessonDao
    abstract fun rewardFlagsDao(): RewardFlagsDao
    abstract fun museumUnlockDao(): MuseumUnlockDao
    abstract fun lessonDao(): LessonDao
    abstract fun lessonExerciseProgressDao(): LessonExerciseProgressDao
    abstract fun lessonRewardClaimsDao(): LessonRewardClaimsDao

    companion object {
        @Volatile
        private var INSTANCE: DracoDatabase? = null

        fun getInstance(context: Context): DracoDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DracoDatabase::class.java,
                    "draco.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
