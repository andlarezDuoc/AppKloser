package cl.duoc.amigo.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
@Database(entities = [Amigo::class, AuthEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun amigoDao(): AmigoDao
    abstract fun authDao(): AuthDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database_kloser" // Nombre del archivo de la DB
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}