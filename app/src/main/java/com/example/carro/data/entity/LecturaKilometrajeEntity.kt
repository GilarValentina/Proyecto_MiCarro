package com.example.carro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/** Lectura histórica del odómetro de un vehículo (RF-06, RF-07). */
@Entity(
    tableName = "lecturas_kilometraje",
    foreignKeys = [
        ForeignKey(
            entity = VehiculoEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehiculoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehiculoId")]
)
data class LecturaKilometrajeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val fecha: LocalDate,
    val valorOdometro: Long,
    val creadoEn: Long = System.currentTimeMillis()
)
