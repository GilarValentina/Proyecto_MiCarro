package com.example.carro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.carro.data.entity.RepuestoEntity
import kotlinx.coroutines.flow.Flow

/** Acceso a datos de repuestos (RF-24 a RF-27). */
@Dao
interface RepuestoDao {

    @Query("SELECT * FROM repuestos WHERE mantenimientoId = :mantenimientoId ORDER BY id ASC")
    fun observarPorMantenimiento(mantenimientoId: Long): Flow<List<RepuestoEntity>>

    @Query("SELECT * FROM repuestos WHERE instaladoActualmente = 1 ORDER BY fechaInstalacion DESC")
    fun observarInstalados(): Flow<List<RepuestoEntity>>

    @Insert
    suspend fun insertar(repuesto: RepuestoEntity): Long

    @Update
    suspend fun actualizar(repuesto: RepuestoEntity)

    @Delete
    suspend fun eliminar(repuesto: RepuestoEntity)
}
