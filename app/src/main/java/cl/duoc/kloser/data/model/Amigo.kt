package cl.duoc.amigo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "amigos")
data class Amigo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val id_xano: Int = 0, // <-- Nuevo campo para guardar el ID de la API
    val nombre: String,
    val autor: String,
    val genero: String
)