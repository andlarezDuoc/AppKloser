package cl.duoc.amigo.repository

import cl.duoc.amigo.model.FormularioModel
import cl.duoc.amigo.model.MensajesError


class  FormularioRepository {

    private var formulario = FormularioModel()
    private var errores = MensajesError()

    fun getFormulario():  FormularioModel = formulario
    fun getMensajesError():  MensajesError = errores


    fun cambiarNombre(nuevoNombre: String) {
        formulario.nombre = nuevoNombre
    }

    fun validacionNombre(): Boolean {
        if(formulario.nombre=="")
            return false
        else
            return true
    }

    fun validacionCorreo(): Boolean {
        if (!formulario.correo.matches(Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$")))
            return false
        else
            return true
    }

    fun validacionContrasena(): Boolean {

        val contrasena = formulario.contrasena
        val minLength = 6

        if (contrasena.isBlank() || contrasena.length < minLength) {
            return false
        } else {
            return true
        } // ⬅️ ¡ESTA LLAVE FALTABA!
    } // ⬅️ Esta llave ahora cierra la función validacionContrasena correctamente.

    fun validacionTerminos(): Boolean {
        if (!formulario.terminos)
            return false
        else
            return true
    }
}