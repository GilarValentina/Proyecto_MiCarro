package com.example.carro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.carro.data.entity.AlertaEntity
import kotlinx.coroutines.flow.Flow

/** Acceso a datos de alertas locales (RF-28 a RF-31, RF-33). */
@Dao
interface AlertaDao {

    @Query("SELECT * FROM alertas WHERE activa = 1 AND atendida = 0 ORDER BY fechaAviso ASC")
    fun observarActivas(): Flow<List<AlertaEntity>>

    @Query("SELECT * FROM alertas WHERE vehiculoId = :vehiculoId ORDER BY fechaAviso ASC")
    fun observarPorVehiculo(vehiculoId: Long): Flow<List<AlertaEntity>>

    @Insert
    suspend fun insertar(alerta: AlertaEntity): Long

    @Update
    suspend fun actualizar(alerta: AlertaEntity)

    @Delete
    suspend fun eliminar(alerta: AlertaEntity)
}
