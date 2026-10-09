package com.example.carro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/** Actividad del plan de mantenimiento preventivo (RF-10 a RF-16). */
@Entity(
    tableName = "actividades_mantenimiento",
    foreignKeys = [
        ForeignKey(
            entity = VehiculoEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehiculoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("vehiculoId"), Index("categoriaId")]
)
data class ActividadMantenimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehiculoId: Long,
    val nombre: String,
    val categoriaId: Long? = null,
    val descripcion: String? = null,
    val programarPorFecha: Boolean = false,
    val programarPorKm: Boolean = false,
    val intervaloDias: Int? = null,
    val intervaloKm: Long? = null,
    val proximaFecha: LocalDate? = null,
    val proximoKm: Long? = null,
    val anticipacionDias: Int? = null,
    val anticipacionKm: Long? = null,
    val estado: String,
    val activa: Boolean = true,
    val creadoEn: Long = System.currentTimeMillis()
)
