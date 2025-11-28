package cl.duoc.amigo.model // Usamos tu paquete de modelos

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "auth_credentials")
data class AuthEntity(
    @PrimaryKey
    val correo: String,
    val nombre: String,
    val contrasena: String
)