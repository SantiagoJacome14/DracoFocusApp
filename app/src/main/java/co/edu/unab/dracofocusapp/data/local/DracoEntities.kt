package co.edu.unab.dracofocusapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecciones_progreso")
data class LeccionProgresoEntity(
    @PrimaryKey val leccionId: String,
    val completada: Boolean = false
)

@Entity(tableName = "piezas_museo")
data class PiezaMuseoEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val imagenRes: Int,
    val bloqueada: Boolean = true
)
