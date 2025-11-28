package cl.duoc.amigo.model

import androidx.room.*
@Dao
interface AmigoDao {
    @Query("SELECT * FROM amigos")
    suspend fun getAll(): List<Amigo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(amigo: Amigo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(amigos: List<Amigo>)

    @Query("DELETE FROM amigos")
    suspend fun deleteAll()

    @Update
    suspend fun update(amigo: Amigo)

    @Delete
    suspend fun delete(amigo: Amigo)
}