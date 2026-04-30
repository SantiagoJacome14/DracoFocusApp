package co.edu.unab.dracofocusapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.edu.unab.dracofocusapp.data.local.CompletedLessonEntity
import co.edu.unab.dracofocusapp.data.local.MuseumUnlockEntity
import co.edu.unab.dracofocusapp.data.local.RewardFlagsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompletedLessonDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CompletedLessonEntity)

    @Query("SELECT lessonId FROM completed_lessons WHERE userId = :userId")
    fun observeCompletedIds(userId: String): Flow<List<String>>

    @Query("SELECT lessonId FROM completed_lessons WHERE userId = :userId")
    suspend fun snapshotLessonIds(userId: String): List<String>
    
    @Query("DELETE FROM completed_lessons WHERE userId = :userId")
    suspend fun clearForUser(userId: String)
}

@Dao
interface RewardFlagsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RewardFlagsEntity)

    @Query("SELECT * FROM reward_flags WHERE userId = :userId LIMIT 1")
    fun observe(userId: String): Flow<RewardFlagsEntity?>

    @Query("SELECT * FROM reward_flags WHERE userId = :userId LIMIT 1")
    suspend fun snapshot(userId: String): RewardFlagsEntity?
}

@Dao
interface MuseumUnlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MuseumUnlockEntity)

    @Query("SELECT pieceCatalogId FROM museum_unlocks WHERE userId = :userId")
    fun observeUnlockedPieceIds(userId: String): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM museum_unlocks WHERE userId = :userId")
    fun observeCollectedCount(userId: String): Flow<Int>

    @Query("SELECT pieceCatalogId FROM museum_unlocks WHERE userId = :userId")
    suspend fun snapshotUnlockedPieceIds(userId: String): List<String>
}
