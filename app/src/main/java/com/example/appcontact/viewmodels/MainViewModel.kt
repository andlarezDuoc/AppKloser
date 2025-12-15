package com.example.appcontact.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appcontact.models.User
import com.example.appcontact.repositories.UserRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _operationStatus = MutableLiveData<String>()
    val operationStatus: LiveData<String> = _operationStatus
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getUsers()
                if (response.isSuccessful) {
                    _users.value = response.body() ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Fallo red/parseo: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addUser(name: String, email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newUser = User(id = null, name = name, email = email)
                val response = repository.addUser(newUser)
                if (response.isSuccessful) {
                    _operationStatus.value = "Contacto agregado"
                    loadUsers()
                } else {
                    _errorMessage.value = "Error al agregar: ${response.code()}"
                }
            } catch (e: Exception) {
                 _errorMessage.value = "Fallo red: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(user: User) {
        if (user.id == null) {
             _errorMessage.value = "Error: Usuario sin ID"
             return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.deleteUser(user.id)
                if (response.isSuccessful) {
                    _operationStatus.value = "Contacto eliminado"
                    loadUsers()
                } else {
                    _errorMessage.value = "Error al eliminar: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Fallo red: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
