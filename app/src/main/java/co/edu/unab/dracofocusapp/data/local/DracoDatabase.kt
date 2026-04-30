package co.edu.unab.dracofocusapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [LeccionProgresoEntity::class, PiezaMuseoEntity::class], version = 1)
abstract class DracoDatabase : RoomDatabase() {
    abstract fun dracoDao(): DracoDao

    companion object {
        @Volatile
        private var INSTANCE: DracoDatabase? = null

        fun getDatabase(context: Context): DracoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DracoDatabase::class.java,
                    "draco_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
