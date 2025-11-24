package cl.duoc.amigo.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.amigo.model.Amigo
import cl.duoc.amigo.repository.AmigoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AmigoViewModel(private var repository: AmigoRepository?) : ViewModel() {

    val amigos = MutableStateFlow<List<Amigo>>(emptyList())

    fun initRepository(repo: AmigoRepository) {
        repository = repo
        viewModelScope.launch {
            amigos.value = repository?.getAll() ?: emptyList()
        }
    }

    fun agregarAmigos(nombre: String, idAmigo: String) {
        viewModelScope.launch {
            repository?.let {
                val nuevoAmigo = Amigo(nombre = nombre, autor = idAmigo, genero = "Amigo")
                it.insert(nuevoAmigo)
                amigos.value = it.getAll()
            }
        }
    }

    fun eliminarAmigo(amigo: Amigo) {
        viewModelScope.launch {
            repository?.let {
                it.delete(amigo)
                amigos.value = it.getAll()
            }
        }
    }
}
