package cl.duoc.amigo.repository

import cl.duoc.amigo.model.Amigo
import cl.duoc.amigo.model.AmigoDao


class AmigoRepository(private val dao: AmigoDao) {
    suspend fun getAll() = dao.getAll()
    suspend fun insert(amigo: Amigo) = dao.insert(amigo)
    suspend fun update(amigo: Amigo) = dao.update(amigo)
    suspend fun delete(amigo: Amigo) = dao.delete(amigo)
}