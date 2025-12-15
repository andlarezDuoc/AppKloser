package cl.duoc.amigo.repository

class FormularioRepository {

    fun validacionNombre(nombre: String): Boolean {
        return nombre.isNotBlank()
    }
    fun validacionCorreo(correo: String): Boolean {
        val emailRegex = Regex("^[\\w.-]+@[\\w.-]+\\.\\w+$")
        return correo.matches(emailRegex)
    }

    fun validacionContrasena(contrasena: String): Boolean {
        val minLength = 6
        return contrasena.isNotBlank() && contrasena.length >= minLength
    }

    fun validacionTerminos(terminos: Boolean): Boolean {

        return terminos
    }
}