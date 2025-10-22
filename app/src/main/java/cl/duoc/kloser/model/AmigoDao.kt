package cl.duoc.amigo.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AmigoDao {
    @Query("SELECT * FROM amigos")
    suspend fun getAll(): List<Amigo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(amigo: Amigo)

    @Update
    suspend fun update(amigo: Amigo)

    @Delete
    suspend fun delete(amigo: Amigo)
}