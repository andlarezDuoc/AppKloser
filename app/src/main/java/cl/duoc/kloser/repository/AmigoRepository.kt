package cl.duoc.amigo.repository

import cl.duoc.amigo.model.Amigo
import cl.duoc.amigo.model.AmigoDao
import cl.duoc.amigo.model.AmigoNetwork
import cl.duoc.kloser.data.remote.ApiService

class AmigoRepository(
    private val dao: AmigoDao,
    private val apiService: ApiService // Inyectamos el servicio de la API
) {

    // Esta función maneja toda la lógica GET (Network -> Cache -> Return)
    suspend fun getAllAmigos(): List<Amigo> {

        // 1. Intentamos obtener de la red (API)
        try {
            val networkList = apiService.getFriends()

            // 2. Mapeo: Convertimos la lista de la API (AmigoNetwork) a la lista de Room (Amigo)
            val localFriends = networkList.map { networkAmigo ->
                Amigo(
                    id_xano = networkAmigo.idXano, // Guardamos el ID de Xano
                    nombre = networkAmigo.nombre,
                    autor = "Sincronizado", // Valores por defecto para Room
                    genero = "API"
                )
            }

            // 3. Sincronizar: Limpiamos la caché antigua e insertamos la nueva
            dao.deleteAll()
            dao.insertAll(localFriends)

            // 4. Devolvemos la lista desde Room (o la lista mapeada, si lo prefieres)
            return localFriends

        } catch (e: Exception) {
            // 5. Si la red falla (No hay internet, Xano caído, etc.)
            println("Error de red: ${e.message}. Devolviendo datos locales (caché).")
            return dao.getAll() // Devolvemos lo que haya en Room
        }
    }

    // Mantenemos estas funciones si son usadas por el ViewModel para la DB local.
    // Si no las usas, puedes borrarlas.
    suspend fun getAll() = dao.getAll()
    suspend fun insert(amigo: Amigo) = dao.insert(amigo)
    suspend fun update(amigo: Amigo) = dao.update(amigo)
    suspend fun delete(amigo: Amigo) = dao.delete(amigo)
}