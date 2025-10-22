package cl.duoc.amigo.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import cl.duoc.amigo.repository.FormularioRepository
import cl.duoc.amigo.model.FormularioModel
import cl.duoc.amigo.model.MensajesError

class FormularioViewModel : ViewModel() {
    private val repository = FormularioRepository()

    var formulario: FormularioModel by mutableStateOf( repository.getFormulario() )
    var mensajesError: MensajesError by mutableStateOf( repository.getMensajesError() )

    fun verificarFormulario(): Boolean {
        return verificarNombre() &&
                verificarCorreo() &&
                verificarContrasena() && // ⬅️ CAMBIADO: verificarContrasena()
                verificarTerminos()
    }

    fun verificarNombre(): Boolean {
        // Tu lógica de verificación de nombre estaba duplicada, la simplifico
        if (!repository.validacionNombre()) {
            mensajesError.nombre = "El nombre no puede estar vacío"
            return false
        } else {
            mensajesError.nombre = ""
            return true
        }
    }

    fun verificarCorreo(): Boolean {
        // Tu lógica de verificación de correo estaba duplicada, la simplifico
        if(!repository.validacionCorreo()) {
            mensajesError.correo = "El correo no es válido"
            return false
        } else {
            mensajesError.correo = ""
            return true
        }
    }

    // 🚀 NUEVA FUNCIÓN: verificarContrasena() (reemplaza a verificarEdad())
    fun verificarContrasena(): Boolean {
        // ASUMO que tienes un método validacionContrasena() en tu repositorio
        if(!repository.validacionContrasena()) {
            // Puedes ajustar este mensaje de error según tus reglas de contraseña (largo mínimo, etc.)
            mensajesError.contrasena = "La contraseña debe tener al menos 6 caracteres"
            return false
        } else {
            mensajesError.contrasena = ""
            return true
        }
    }

    fun verificarTerminos(): Boolean {
        // Tu lógica de verificación de términos estaba duplicada, la simplifico
        if(!repository.validacionTerminos()) {
            mensajesError.terminos = "Debes aceptar los términos"
            return false
        } else {
            mensajesError.terminos = ""
            return true
        }
    }
}