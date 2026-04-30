package co.edu.unab.dracofocusapp.data

import android.content.Context
import co.edu.unab.dracofocusapp.data.local.DracoDatabase
import co.edu.unab.dracofocusapp.data.local.LeccionProgresoEntity
import kotlinx.coroutines.flow.first

class RewardManager(private val context: Context) {
    private val dao = DracoDatabase.getDatabase(context).dracoDao()

    suspend fun completarLeccion(leccionId: String): String? {
        // Guardar progreso local
        dao.insertLeccionProgreso(LeccionProgresoEntity(leccionId, true))
        
        // Verificar si se completaron los fundamentos (ejemplo: primeras 3 lecciones)
        val completadas = dao.getCompletadasCount().first()
        
        return if (completadas == 3) {
            // Desbloquear primera pieza del museo como recompensa
            dao.desbloquearPieza("1")
            "¡Felicidades! Has completado los fundamentos. Recibiste un Sobre Misterioso y desbloqueaste una pieza en el Museo."
        } else {
            null
        }
    }
}
