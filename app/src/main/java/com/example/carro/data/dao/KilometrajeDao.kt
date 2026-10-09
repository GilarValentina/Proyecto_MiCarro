package com.example.carro.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.carro.data.entity.LecturaKilometrajeEntity
import kotlinx.coroutines.flow.Flow

/** Acceso a datos de lecturas de kilometraje (RF-06 a RF-09). */
@Dao
interface KilometrajeDao {

    @Query("SELECT * FROM lecturas_kilometraje WHERE vehiculoId = :vehiculoId ORDER BY fecha DESC, id DESC")
    fun observarPorVehiculo(vehiculoId: Long): Flow<List<LecturaKilometrajeEntity>>

    @Query("SELECT MAX(valorOdometro) FROM lecturas_kilometraje WHERE vehiculoId = :vehiculoId")
    suspend fun obtenerMaximoOdometro(vehiculoId: Long): Long?

    @Insert
    suspend fun insertar(lectura: LecturaKilometrajeEntity): Long
}
