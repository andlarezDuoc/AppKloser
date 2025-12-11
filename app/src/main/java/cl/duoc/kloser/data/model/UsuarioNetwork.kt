package cl.duoc.kloser.data.model

import com.google.gson.annotations.SerializedName

data class UsuarioNetwork(
    @SerializedName("friend_id") // We might need to change this if Xano endpoint changes, but keeping for now as 'friend_id' might be the key on the server or we need to update it to what Xano uses.
    val idXano: Int,

    @SerializedName("name")
    val nombre: String,
    
    @SerializedName("email")
    val email: String
)