package cl.duoc.amigo.model

data class FormularioModel(
    var nombre: String = "",
    var correo: String = "",
    var contrasena: String = "",
    var terminos: Boolean = false
)