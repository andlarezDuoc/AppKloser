package cl.duoc.amigo.repository

import cl.duoc.amigo.model.Amigo
import cl.duoc.amigo.model.AmigoDao
import cl.duoc.amigo.model.AmigoNetwork
import cl.duoc.kloser.data.remote.ApiService

class AmigoRepository(
    private val dao: AmigoDao,
    private val apiService: ApiService
) {

    suspend fun getFriendById(idXano: Int): Amigo? {
        return try {
            val networkAmigo = apiService.getFriendById(idXano)

            // Mapear AmigoNetwork a Amigo (Local)
            Amigo(
                id = 0,
                id_xano = networkAmigo.idXano,
                nombre = networkAmigo.nombre,
                autor = "Buscado",
                genero = "API"
            )
        } catch (e: Exception) {
            println("Error en Repositorio al buscar ID $idXano: ${e.message}")
            // Si falla la red o el ID no existe (404), devuelve null
            null
        }
    }

    suspend fun getFriendsFromApi(): List<Amigo> {
        return try {
            val networkAmigos = apiService.getFriends()
            networkAmigos.map { networkAmigo ->
                Amigo(
                    id = 0,
                    id_xano = networkAmigo.idXano,
                    nombre = networkAmigo.nombre,
                    autor = "API",
                    genero = "API"
                )
            }
        } catch (e: Exception) {
            println("Error getting friends from API: ${e.message}")
            emptyList()
        }
    }


    suspend fun insert(amigo: Amigo) {
        val networkModel = AmigoNetwork(idXano = 0, nombre = amigo.nombre)

        try {
            val newAmigoNetwork = apiService.addFriend(networkModel)

            val amigoConIdXano = Amigo(
                id = 0,
                id_xano = newAmigoNetwork.idXano,
                nombre = newAmigoNetwork.nombre,
                autor = amigo.autor,
                genero = amigo.genero
            )

            dao.insert(amigoConIdXano)

        } catch (e: Exception) {
            println("Fallo al insertar amigo: ${e.message}")
            dao.insert(amigo)
        }
    }

    suspend fun delete(amigo: Amigo) {
        try {
            dao.delete(amigo)

        } catch (e: Exception) {
            println("Fallo al eliminar amigo con ID Xano ${amigo.id_xano}. Error: ${e.message}")
        }
    }
    suspend fun getAll() = dao.getAll()
}