package com.example.appcontact.api

import com.example.appcontact.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("usuario")
    suspend fun getUsers(): Response<List<User>>

    @POST("usuario")
    suspend fun addUser(@Body user: User): Response<User>

    @DELETE("usuario/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Void>
}
