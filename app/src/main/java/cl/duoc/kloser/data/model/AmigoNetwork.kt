package cl.duoc.amigo.model

import com.google.gson.annotations.SerializedName

data class AmigoNetwork(
    @SerializedName("friend_id")
    val idXano: Int,

    @SerializedName("name")
    val nombre: String
)