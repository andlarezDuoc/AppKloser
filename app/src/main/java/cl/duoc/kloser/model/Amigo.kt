package cl.duoc.amigo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "amigos")
data class Amigo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val autor: String, // Usado para el ID del amigo a agregar
    val genero: String
)