package cl.duoc.kloser.repository

import cl.duoc.kloser.data.model.Usuario
import cl.duoc.kloser.data.model.UsuarioNetwork
import cl.duoc.kloser.data.remote.ApiService

class UsuarioRepository(
    private val apiService: ApiService
) {

    // Buscar por nombre (External API)
    suspend fun getUsuariosByName(nombre: String): List<Usuario> {
        return try {
            val networkUsuarios = apiService.getUsuariosByName(nombre)
            networkUsuarios.map { networkUsuario ->
                Usuario(
                    id = 0, // No local ID
                    id_xano = networkUsuario.idXano,
                    nombre = networkUsuario.nombre,
                    email = networkUsuario.email,
                    foto = "" // Placeholder or map if available
                )
            }
        } catch (e: Exception) {
            println("Error en Repositorio al buscar por nombre $nombre: ${e.message}")
            emptyList()
        }
    }

    suspend fun getUsuariosFromApi(): List<Usuario> {
        return try {
            val networkUsuarios = apiService.getUsuarios()
            networkUsuarios.map { networkUsuario ->
                Usuario(
                    id = 0,
                    id_xano = networkUsuario.idXano,
                    nombre = networkUsuario.nombre,
                    email = networkUsuario.email,
                    foto = ""
                )
            }
        } catch (e: Exception) {
            println("Error getting users from API: ${e.message}")
            emptyList()
        }
    }


    suspend fun insert(usuario: Usuario) {
        val networkModel = UsuarioNetwork(idXano = 0, nombre = usuario.nombre, email = usuario.email)

        try {
            apiService.addUsuario(networkModel)
        } catch (e: Exception) {
            println("Fallo al insertar usuario: ${e.message}")
        }
    }

    // Delete is not mentioned for external API in the plan or initial code for 'Amigo',
    // and usually deleting external users might require different permissions or endpoints.
    // Keeping it empty or removing it as 'AmigoDao' logic is gone.
    // User requested: "limpia el codigo y elimina codigo basura".
    // I will remove delete if it was only local.
}