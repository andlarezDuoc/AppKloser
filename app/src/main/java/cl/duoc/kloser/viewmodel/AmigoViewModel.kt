package cl.duoc.amigo.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.amigo.model.Amigo
import cl.duoc.amigo.repository.AmigoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


class AmigoViewModel(private val repository: AmigoRepository) : ViewModel() {

    val amigos = MutableStateFlow<List<Amigo>>(emptyList())

    init {
        cargarAmigos()
    }

    private fun cargarAmigos() {
        viewModelScope.launch {
            amigos.value = repository.getAll()
        }
    }

    fun agregarAmigos(nombre: String, idAmigo: String) {
        viewModelScope.launch {
            val nuevoAmigo = Amigo(
                nombre = nombre,
                autor = idAmigo,
                genero = "Amigo"
            )
            repository.insert(nuevoAmigo)
            cargarAmigos()
        }
    }

    // Puedes dejar otras funciones (update, delete) si las tienes, usando 'amigo'
    fun eliminarAmigo(amigo: Amigo) {
        viewModelScope.launch {
            repository.delete(amigo)
            cargarAmigos()
        }
    }
}