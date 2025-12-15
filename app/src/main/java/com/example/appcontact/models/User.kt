package com.example.appcontact.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int?,
    @SerializedName("nombre") val name: String?,
    @SerializedName("email") val email: String?
)
