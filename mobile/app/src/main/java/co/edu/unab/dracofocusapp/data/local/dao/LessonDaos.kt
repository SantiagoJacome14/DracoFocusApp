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

    @Query("SELECT lessonId FROM completed_lessons")
    fun observeCompletedIds(): Flow<List<String>>

    @Query("SELECT lessonId FROM completed_lessons")
    suspend fun snapshotLessonIds(): List<String>
}

@Dao
interface RewardFlagsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RewardFlagsEntity)

    @Query("SELECT * FROM reward_flags WHERE id = :id LIMIT 1")
    fun observe(id: Int = 0): Flow<RewardFlagsEntity?>

    @Query("SELECT * FROM reward_flags WHERE id = :id LIMIT 1")
    suspend fun snapshot(id: Int = 0): RewardFlagsEntity?
}

@Dao
interface MuseumUnlockDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: MuseumUnlockEntity)

    @Query("SELECT pieceCatalogId FROM museum_unlocks")
    fun observeUnlockedPieceIds(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM museum_unlocks")
    fun observeCollectedCount(): Flow<Int>

    @Query("SELECT pieceCatalogId FROM museum_unlocks")
    suspend fun snapshotUnlockedPieceIds(): List<String>
}
