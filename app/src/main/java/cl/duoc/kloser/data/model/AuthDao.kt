package cl.duoc.amigo.model // Usamos tu paquete de modelos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AuthDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AuthEntity)

    @Query("SELECT * FROM auth_credentials WHERE correo = :correo")
    suspend fun getUserByEmail(correo: String): AuthEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM auth_credentials WHERE correo = :correo LIMIT 1)")
    suspend fun existsByEmail(correo: String): Boolean
}