package cl.duoc.kloser.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val id_xano: Int = 0,
    val nombre: String,
    val email: String,
    val foto: String
)