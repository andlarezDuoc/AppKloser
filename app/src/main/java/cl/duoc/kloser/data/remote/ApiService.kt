package cl.duoc.kloser.data.remote

import cl.duoc.amigo.model.AmigoNetwork
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/")
    suspend fun getFriendById(@Query("friend_id") idXano: Int): AmigoNetwork

    @GET("/")
    suspend fun getFriends(): List<AmigoNetwork>

    @POST("/")
    suspend fun addFriend(@Body amigo: AmigoNetwork): AmigoNetwork

}