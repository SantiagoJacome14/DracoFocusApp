package co.edu.unab.dracofocusapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "completed_lessons")
data class CompletedLessonEntity(
    @PrimaryKey val lessonId: String,
    val completedAtMillis: Long,
)

@Entity(tableName = "reward_flags")
data class RewardFlagsEntity(
    @PrimaryKey val id: Int = 0,
    val soloFundamentosEnvelopeClaimed: Boolean = false,
)

@Entity(tableName = "museum_unlocks")
data class MuseumUnlockEntity(
    @PrimaryKey val pieceCatalogId: String,
    val unlockedAtMillis: Long,
)
