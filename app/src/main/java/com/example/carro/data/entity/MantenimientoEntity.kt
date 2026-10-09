package com.example.carro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/** Mantenimiento realizado, preventivo o correctivo (RF-17 a RF-23). */
@Entity(
    tableName = "mantenimientos",
    foreignKeys = [
        ForeignKey(
            entity = VehiculoEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehiculoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ActividadMantenimientoEntity::class,
            parentColumns = ["id"],
            childColumns = ["actividadId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("vehiculoId"), Index("actividadId")]
)
data class MantenimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val actividadId: Long? = null,
    val fecha: LocalDate,
    val kilometraje: Long,
    val tipo: String,
    val descripcion: String,
    val tallerResponsable: String? = null,
    val costoManoObra: Double = 0.0,
    val costoRepuestos: Double = 0.0,
    val costoTotal: Double = 0.0,
    val pendienteSync: Boolean = false,
    val creadoEn: Long = System.currentTimeMillis()
)
