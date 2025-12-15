package cl.duoc.kloser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.kloser.data.model.Usuario
import cl.duoc.kloser.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel que maneja el estado de la UI y se comunica con el Repositorio (Solo Retrofit/Xano).
 */
class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    // --- ESTADO PARA LA LISTA COMPLETA ---
    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios

    // --- ESTADO PARA LA BÚSQUEDA ---
    // Change to List<Usuario> as search by name can return multiple
    private val _searchedUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val searchedUsuarios: StateFlow<List<Usuario>> = _searchedUsuarios

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    // --- INICIALIZACIÓN ---
    init {
        fetchUsuarios()
    }

    // 1. OBTENER DATOS (GET)
    fun fetchUsuarios() {
        viewModelScope.launch {
            try {
                _usuarios.value = repository.getUsuariosFromApi()
            } catch (e: Exception) {
                println("Error de carga inicial de usuarios: ${e.message}")
            }
        }
    }

    // 2. BUSCAR USUARIO (GET por Nombre)
    fun searchUsuario(nombre: String) {
        viewModelScope.launch {
            if (nombre.isBlank()) {
                _searchedUsuarios.value = emptyList()
                return@launch
            }

            _isSearching.value = true
            _searchedUsuarios.value = emptyList()

            try {
                _searchedUsuarios.value = repository.getUsuariosByName(nombre)
            } catch (e: Exception) {
                println("Error al buscar usuario: ${e.message}")
                _searchedUsuarios.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }

    // 3. AGREGAR USUARIO (POST)
    fun agregarUsuario(nombre: String, email: String) {
        viewModelScope.launch {
            try {
                val nuevoUsuario = Usuario(
                    nombre = nombre,
                    email = email,
                    foto = ""
                )

                repository.insert(nuevoUsuario)

                // Recargamos la lista
                fetchUsuarios()
            } catch (e: Exception) {
                println("Error al agregar usuario: ${e.message}")
            }
        }
    }
}