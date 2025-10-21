package cl.duoc.kloser.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.kloser.model.User
import cl.duoc.kloser.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: MediaRepository = MediaRepository()) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    val images = repository.images
    fun login(username: String) {
        viewModelScope.launch {
            _user.value = User(id = username.hashCode().toString(), username = username, displayName = username)
        }
    }

    fun logout() {
        _user.value = null
    }
    fun addImage(bitmap: android.graphics.Bitmap) {
        repository.addImage(bitmap)
    }
}