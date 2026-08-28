package co.edu.unab.dracofocusapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lessons")
data class LessonEntity(
    @PrimaryKey val id: Int,
    val slug: String,
    val title: String,
    @ColumnInfo(name = "xp_reward") val xpReward: Int = 0
)

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

/** Tracks which lessons have already granted a museum reward (one reward per lesson, ever). */
@Entity(
    tableName = "lesson_reward_claims",
    primaryKeys = ["userId", "lessonSlug"]
)
data class LessonRewardClaimEntity(
    val userId: String,
    val lessonSlug: String,
    val claimedAtMillis: Long,
)

/** Persists the last correctly-answered exercise index per lesson, per user. */
@Entity(
    tableName = "lesson_exercise_progress",
    primaryKeys = ["userId", "lessonSlug"]
)
data class LessonExerciseProgressEntity(
    val userId: String,
    val lessonSlug: String,
    val currentIndex: Int,
)

/**
 * Cache local del contenido de los ejercicios de una lección (pregunta, tipo, datos JSON).
 * Permite abrir una lección ya vista antes sin esperar al backend. El campo `data` de cada
 * ejercicio es JSON libre (varía por tipo: quiz, puzzle, relleno), se guarda serializado
 * como texto en dataJson.
 */
@Entity(
    tableName = "lesson_exercises_cache",
    primaryKeys = ["lessonSlug", "exerciseId"]
)
data class LessonExerciseCacheEntity(
    val lessonSlug: String,
    val exerciseId: Int,
    val type: String,
    val question: String,
    val dataJson: String?,
    val hint: String?,
    val sortOrder: Int,
)
