package cl.duoc.amigo.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Amigo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun amigoDao(): AmigoDao
}