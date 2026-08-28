package co.edu.unab.dracofocusapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import co.edu.unab.dracofocusapp.data.local.dao.CompletedLessonDao
import co.edu.unab.dracofocusapp.data.local.dao.LessonDao
import co.edu.unab.dracofocusapp.data.local.dao.LessonExerciseCacheDao
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
        LessonExerciseCacheEntity::class,
    ],
    version = 6  // bumped: added lesson_exercises_cache table (cache local de ejercicios)
)
abstract class DracoDatabase : RoomDatabase() {

    abstract fun completedLessonDao(): CompletedLessonDao
    abstract fun rewardFlagsDao(): RewardFlagsDao
    abstract fun museumUnlockDao(): MuseumUnlockDao
    abstract fun lessonDao(): LessonDao
    abstract fun lessonExerciseProgressDao(): LessonExerciseProgressDao
    abstract fun lessonRewardClaimsDao(): LessonRewardClaimsDao
    abstract fun lessonExerciseCacheDao(): LessonExerciseCacheDao

    companion object {
        /**
         * v5 -> v6: agrega SOLO la tabla lesson_exercises_cache (cache local de ejercicios).
         * No toca ninguna tabla existente: progreso, lecciones completadas, recompensas y
         * museo se conservan intactos al actualizar.
         */
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `lesson_exercises_cache` (
                        `lessonSlug` TEXT NOT NULL,
                        `exerciseId` INTEGER NOT NULL,
                        `type` TEXT NOT NULL,
                        `question` TEXT NOT NULL,
                        `dataJson` TEXT,
                        `hint` TEXT,
                        `sortOrder` INTEGER NOT NULL,
                        PRIMARY KEY(`lessonSlug`, `exerciseId`)
                    )
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: DracoDatabase? = null

        fun getInstance(context: Context): DracoDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DracoDatabase::class.java,
                    "draco.db"
                )
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
