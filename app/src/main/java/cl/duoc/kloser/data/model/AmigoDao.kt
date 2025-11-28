package cl.duoc.amigo.model

import androidx.room.*
@Dao
interface AmigoDao {
    @Query("SELECT * FROM amigos")
    suspend fun getAll(): List<Amigo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(amigo: Amigo)

    // Función para insertar una lista completa de una sola vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(amigos: List<Amigo>)

    @Query("DELETE FROM amigos")
    suspend fun deleteAll() // Función para limpiar la caché

    // No necesitamos update ni delete en el repositorio si no se usan,
    // pero las mantendremos en el DAO por si acaso.
    @Update
    suspend fun update(amigo: Amigo)

    @Delete
    suspend fun delete(amigo: Amigo)
}