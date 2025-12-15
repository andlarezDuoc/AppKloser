package cl.duoc.kloser.data.remote

import cl.duoc.amigo.model.AmigoNetwork
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Buscar usuarios por nombre en Xano
    // Assuming the endpoint accepts a query param for name filtering.
    // Xano usually allows filtering like ?name=... or via specific endpoints.
    // Based on user request: "buscar por el nombre y no por el ID"
    @GET("/usuario") 
    suspend fun getUsuariosByName(@Query("name") nombre: String): List<UsuarioNetwork>

    @GET("/usuario")
    suspend fun getUsuarios(): List<UsuarioNetwork>

    @POST("/usuario")
    suspend fun addUsuario(@Body usuario: UsuarioNetwork): UsuarioNetwork

}