package co.edu.unab.dracofocusapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import co.edu.unab.dracofocusapp.data.local.dao.CompletedLessonDao
import co.edu.unab.dracofocusapp.data.local.dao.LessonDao
import co.edu.unab.dracofocusapp.data.local.dao.MuseumUnlockDao
import co.edu.unab.dracofocusapp.data.local.dao.RewardFlagsDao

@Database(
    exportSchema = false,
    entities = [
        CompletedLessonEntity::class,
        RewardFlagsEntity::class,
        MuseumUnlockEntity::class,
        LessonEntity::class,
    ],
    version = 3, // Incrementado para agregar LessonEntity
)
abstract class DracoDatabase : RoomDatabase() {

    abstract fun completedLessonDao(): CompletedLessonDao
    abstract fun rewardFlagsDao(): RewardFlagsDao
    abstract fun museumUnlockDao(): MuseumUnlockDao
    abstract fun lessonDao(): LessonDao
}
