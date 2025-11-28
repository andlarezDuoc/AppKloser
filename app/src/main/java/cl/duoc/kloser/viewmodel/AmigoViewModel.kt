package cl.duoc.amigo.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.amigo.model.Amigo
import cl.duoc.amigo.repository.AmigoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que maneja el estado de la UI y se comunica con el Repositorio.
 * Recibe el AmigoRepository a través de inyección en el constructor.
 */
class AmigoViewModel(private val repository: AmigoRepository) : ViewModel() {

    // MutableStateFlow privado para actualizar la lista.
    private val _amigos = MutableStateFlow<List<Amigo>>(emptyList())

    // StateFlow público que la UI (Composable/Activity) observará.
    val amigos: StateFlow<List<Amigo>> = _amigos

    // Cargamos los amigos automáticamente al inicializar el ViewModel
    init {
        fetchAmigos()
    }

    // 1. Obtener datos (GET) 📞
    fun fetchAmigos() {
        viewModelScope.launch {
            try {
                // Llama al Repositorio, que maneja la lógica de Network (Xano) -> Cache (Room).
                _amigos.value = repository.getAllAmigos()
            } catch (e: Exception) {
                // Manejo de errores de red/db. (Mantiene la última lista en caso de error)
                println("Error de carga de amigos: ${e.message}")
            }
        }
    }

    // 2. Agregar amigo (POST) ➕
    fun agregarAmigo(nombre: String) {
        viewModelScope.launch {
            try {
                // Creamos un objeto temporal. El Repositorio debe encargarse
                // de enviarlo a Xano y guardarlo en Room.
                val nuevoAmigo = Amigo(
                    nombre = nombre,
                    autor = "Usuario",
                    genero = "Amigo"
                    // id y id_xano se manejan en el Repository
                )

                repository.insert(nuevoAmigo)

                // Volvemos a cargar la lista para que la UI se actualice con el nuevo amigo.
                fetchAmigos()
            } catch (e: Exception) {
                println("Error al agregar amigo: ${e.message}")
            }
        }
    }

    // 3. Eliminar amigo (DELETE) 🗑️
    fun eliminarAmigo(amigo: Amigo) {
        viewModelScope.launch {
            try {
                // Llama al Repositorio para que elimine de Room (y potencialmente de Xano,
                // dependiendo de cómo implementes el delete en el Repository).
                repository.delete(amigo)

                // Volvemos a cargar la lista para actualizar la UI.
                fetchAmigos()
            } catch (e: Exception) {
                println("Error al eliminar amigo: ${e.message}")
            }
        }
    }
}