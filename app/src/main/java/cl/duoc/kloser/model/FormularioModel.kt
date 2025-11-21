package cl.duoc.amigo.model


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class FormularioModel(
    // Nota: Usamos var para que el ViewModel pueda cambiarlas
    var nombre: String = "",
    var correo: String = "",
    var contrasena: String = "",
    var terminos: Boolean = false
)