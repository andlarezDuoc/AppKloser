package cl.duoc.kloser.data.remote

import cl.duoc.amigo.model.AmigoNetwork // <-- Usamos el modelo de red
import retrofit2.http.GET

interface ApiService {

    // Función GET para obtener la lista de amigos
    @GET("/")
    suspend fun getFriends(): List<AmigoNetwork>
}