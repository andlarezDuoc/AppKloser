package cl.duoc.amigo.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.amigo.model.Amigo
import cl.duoc.amigo.repository.AmigoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que maneja el estado de la UI y se comunica con el Repositorio (Room/Retrofit).
 */
class AmigoViewModel(private val repository: AmigoRepository) : ViewModel() {

    // --- ESTADO PARA LA LISTA COMPLETA (USADO POR LazyColumn) ---
    private val _amigos = MutableStateFlow<List<Amigo>>(emptyList())
    val amigos: StateFlow<List<Amigo>> = _amigos // ⚠️ Variable pública que usa Amigos.kt

    // --- ESTADO PARA LA BÚSQUEDA ---
    private val _searchedFriend = MutableStateFlow<Amigo?>(null)
    val searchedFriend: StateFlow<Amigo?> = _searchedFriend

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    // --- INICIALIZACIÓN ---
    init {
        // Carga la lista de amigos de Room/API inmediatamente al inicio
        fetchAmigos()
    }

    // --- FUNCIONES DE OPERACIÓN ---

    // 1. OBTENER DATOS (GET & SYNC)
    fun fetchAmigos() {
        viewModelScope.launch {
            try {
                // Llama al Repositorio, que trae datos de la API y los guarda en Room.
                _amigos.value = repository.getAll()
            } catch (e: Exception) {
                println("Error de carga inicial de amigos: ${e.message}")
            }
        }
    }

    // 2. BUSCAR UN AMIGO (GET por ID)
    fun searchFriend(idString: String) {
        viewModelScope.launch {
            if (idString.isBlank()) return@launch

            _isSearching.value = true
            _searchedFriend.value = null // Limpiar el resultado anterior

            try {
                val idXano = idString.toIntOrNull()

                if (idXano != null) {
                    // Llama al repositorio con el ID de Xano
                    val friend = repository.getFriendById(idXano)
                    _searchedFriend.value = friend
                } else {
                    println("Búsqueda fallida: ID no válido.")
                }
            } catch (e: Exception) {
                println("Error al buscar amigo: ${e.message}")
                _searchedFriend.value = null
            } finally {
                _isSearching.value = false
            }
        }
    }

    // 3. AGREGAR AMIGO (POST)
    fun agregarAmigo(nombre: String) {
        viewModelScope.launch {
            try {
                // Creamos un objeto temporal (el ID lo genera Xano)
                val nuevoAmigo = Amigo(
                    nombre = nombre,
                    autor = "Usuario",
                    genero = "Manual"
                )

                repository.insert(nuevoAmigo)

                // Recargamos la lista para actualizar la UI
                fetchAmigos()
            } catch (e: Exception) {
                println("Error al agregar amigo: ${e.message}")
            }
        }
    }

    // 4. ELIMINAR AMIGO (DELETE)
    fun eliminarAmigo(amigo: Amigo) {
        viewModelScope.launch {
            try {
                // Llama al Repositorio para eliminar de Room y/o API
                repository.delete(amigo)

                // Recargamos la lista para actualizar la UI
                fetchAmigos()
            } catch (e: Exception) {
                println("Error al eliminar amigo: ${e.message}")
            }
        }
    }
}