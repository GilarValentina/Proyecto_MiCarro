package com.example.carro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.carro.data.entity.DocumentoEntity
import kotlinx.coroutines.flow.Flow

/** Acceso a datos de documentos legales (RF-32, RF-33). */
@Dao
interface DocumentoDao {

    @Query("SELECT * FROM documentos WHERE vehiculoId = :vehiculoId ORDER BY fechaVencimiento ASC")
    fun observarPorVehiculo(vehiculoId: Long): Flow<List<DocumentoEntity>>

    @Query("SELECT * FROM documentos ORDER BY fechaVencimiento ASC")
    fun observarTodos(): Flow<List<DocumentoEntity>>

    @Query("SELECT * FROM documentos ORDER BY fechaVencimiento ASC")
    suspend fun obtenerTodos(): List<DocumentoEntity>

    @Query("SELECT * FROM documentos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): DocumentoEntity?

    @Insert
    suspend fun insertar(documento: DocumentoEntity): Long

    @Update
    suspend fun actualizar(documento: DocumentoEntity)

    @Delete
    suspend fun eliminar(documento: DocumentoEntity)
}
