package co.edu.unab.dracofocusapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DracoDao {
    // Lecciones
    @Query("SELECT * FROM lecciones_progreso")
    fun getAllLeccionesProgreso(): Flow<List<LeccionProgresoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeccionProgreso(progreso: LeccionProgresoEntity)

    @Query("SELECT COUNT(*) FROM lecciones_progreso WHERE completada = 1")
    fun getCompletadasCount(): Flow<Int>

    // Museo
    @Query("SELECT * FROM piezas_museo")
    fun getAllPiezasMuseo(): Flow<List<PiezaMuseoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPiezas(piezas: List<PiezaMuseoEntity>)

    @Query("UPDATE piezas_museo SET bloqueada = 0 WHERE id = :piezaId")
    suspend fun desbloquearPieza(piezaId: String)
}
