package cl.duoc.amigo.model

import com.google.gson.annotations.SerializedName

data class AmigoNetwork(
    // Clave de ID que devuelve Xano.
    @SerializedName("friend_id")
    val idXano: Int,

    // Clave de Nombre que devuelve Xano.
    @SerializedName("name")
    val nombre: String
)