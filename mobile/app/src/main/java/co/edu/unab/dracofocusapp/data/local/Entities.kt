package co.edu.unab.dracofocusapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "completed_lessons",
    primaryKeys = ["userId", "lessonId"]
)
data class CompletedLessonEntity(
    val userId: String,
    val lessonId: String,
    val completedAtMillis: Long,
)

@Entity(tableName = "reward_flags")
data class RewardFlagsEntity(
    @PrimaryKey val userId: String,
    val soloFundamentosEnvelopeClaimed: Boolean = false,
)

@Entity(
    tableName = "museum_unlocks",
    primaryKeys = ["userId", "pieceCatalogId"]
)
data class MuseumUnlockEntity(
    val userId: String,
    val pieceCatalogId: String,
    val unlockedAtMillis: Long,
)
