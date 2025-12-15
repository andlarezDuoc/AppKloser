package com.example.appcontact.repositories

import com.example.appcontact.api.RetrofitClient
import com.example.appcontact.models.User
import retrofit2.Response

class UserRepository {
    private val api = RetrofitClient.instance

    suspend fun getUsers(): Response<List<User>> {
        return api.getUsers()
    }

    suspend fun addUser(user: User): Response<User> {
        return api.addUser(user)
    }

    suspend fun deleteUser(id: Int): Response<Void> {
        return api.deleteUser(id)
    }
}
