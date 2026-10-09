package com.example.carro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/** Alerta local generada para un mantenimiento o documento (RF-28 a RF-33). */
@Entity(
    tableName = "alertas",
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
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val actividadId: Long? = null,
    val documentoId: Long? = null,
    val tipo: String,
    val causa: String,
    val fechaAviso: LocalDate,
    val pospuestaHasta: LocalDate? = null,
    val activa: Boolean = true,
    val atendida: Boolean = false,
    val creadoEn: Long = System.currentTimeMillis()
)
