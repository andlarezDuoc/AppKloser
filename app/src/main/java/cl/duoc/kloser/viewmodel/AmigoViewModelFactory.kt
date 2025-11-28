package cl.duoc.amigo.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.amigo.repository.AmigoRepository

class AmigoViewModelFactory(
    private val repository: AmigoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AmigoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AmigoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}