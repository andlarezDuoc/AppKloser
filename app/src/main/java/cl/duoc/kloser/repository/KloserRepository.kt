package cl.duoc.kloser.repository

import android.graphics.Bitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MediaRepository {
    private val _images = MutableStateFlow<List<Bitmap>>(emptyList())
    val images: StateFlow<List<Bitmap>> = _images

    fun addImage(bitmap: Bitmap) {
        _images.value = _images.value + bitmap
    }

    fun clear() {
        _images.value = emptyList()
    }
}