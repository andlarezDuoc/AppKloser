package cl.duoc.kloser.data.model

import com.google.gson.annotations.SerializedName

data class Friend(

    @SerializedName("friend_id")
    val id: Int,

    val name: String
)