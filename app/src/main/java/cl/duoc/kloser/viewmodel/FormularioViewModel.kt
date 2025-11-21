package cl.duoc.amigo.viewModel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.amigo.repository.AuthRepository
import cl.duoc.amigo.repository.FormularioRepository
import cl.duoc.amigo.model.FormularioModel
import cl.duoc.amigo.model.MensajesError
import cl.duoc.amigo.model.AppDatabase
import kotlinx.coroutines.launch

class FormularioViewModel(context: Context) : ViewModel() {

    private val formularioRepository = FormularioRepository()

    private lateinit var authRepository: AuthRepository

    init {
        val authDao = AppDatabase.getDatabase(context).authDao()
        authRepository = AuthRepository(authDao)
    }

    // Estados
    var formulario: FormularioModel by mutableStateOf( FormularioModel() )
    var mensajesError: MensajesError by mutableStateOf( MensajesError() )
    var registroExitoso by mutableStateOf(false)


    fun registrarUsuario() {
        if (!verificarFormulario()) {
            return
        }

        viewModelScope.launch {
            registroExitoso = authRepository.registerUser(formulario)
        }
    }


    suspend fun iniciarSesion(correo: String, contrasena: String): Boolean {
        return authRepository.loginUser(correo, contrasena)
    }

    fun verificarFormulario(): Boolean {
        verificarNombre()
        verificarCorreo()
        verificarContrasena()
        verificarTerminos()

        return mensajesError.nombre.isEmpty() &&
                mensajesError.correo.isEmpty() &&
                mensajesError.contrasena.isEmpty() &&
                mensajesError.terminos.isEmpty()
    }

    fun verificarNombre(): Boolean {
        if (!formularioRepository.validacionNombre(formulario.nombre)) {
            mensajesError = mensajesError.copy(nombre = "El nombre no puede estar vacío")
            return false
        } else {
            mensajesError = mensajesError.copy(nombre = "")
            return true
        }
    }

    fun verificarCorreo(): Boolean {
        if(!formularioRepository.validacionCorreo(formulario.correo)) {
            mensajesError = mensajesError.copy(correo = "El correo no es válido")
            return false
        } else {
            mensajesError = mensajesError.copy(correo = "")
            return true
        }
    }


    fun verificarContrasena(): Boolean {
        if(!formularioRepository.validacionContrasena(formulario.contrasena)) {
            mensajesError = mensajesError.copy(contrasena = "La contraseña debe tener al menos 6 caracteres")
            return false
        } else {
            mensajesError = mensajesError.copy(contrasena = "")
            return true
        }
    }

    fun verificarTerminos(): Boolean {
        if(!formularioRepository.validacionTerminos(formulario.terminos)) {
            mensajesError = mensajesError.copy(terminos = "Debes aceptar los términos")
            return false
        } else {
            mensajesError = mensajesError.copy(terminos = "")
            return true
        }
    }
}