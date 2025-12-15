package cl.duoc.amigo.repository

import android.util.Log
import cl.duoc.amigo.model.AuthDao
import cl.duoc.amigo.model.AuthEntity
import cl.duoc.amigo.model.FormularioModel

class AuthRepository(private val authDao: AuthDao) {

    suspend fun registerUser(form: FormularioModel): Boolean {
        return try {
            if (authDao.existsByEmail(form.correo)) {
                Log.w("AuthRepository", "El correo ${form.correo} ya está registrado localmente.")
                return false
            }
            val newEntity = AuthEntity(
                correo = form.correo,
                nombre = form.nombre,
                contrasena = form.contrasena
            )
            authDao.insertUser(newEntity)
            Log.d("AuthRepository", "Usuario ${form.correo} registrado y guardado en Room.")
            true
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al registrar usuario en Room", e)
            false
        }
    }

    suspend fun loginUser(correo: String, contrasena: String): Boolean {
        return try {
            val userEntity = authDao.getUserByEmail(correo)

            if (userEntity != null) {
                val success = userEntity.contrasena == contrasena
                if (success) {
                    Log.d("AuthRepository", "Inicio de sesión exitoso para $correo (Room).")
                } else {
                    Log.w("AuthRepository", "Contraseña incorrecta para $correo (Room).")
                }
                return success
            } else {
                Log.w("AuthRepository", "Usuario $correo no encontrado en Room.")
                false
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error al iniciar sesión desde Room", e)
            false
        }
    }
}