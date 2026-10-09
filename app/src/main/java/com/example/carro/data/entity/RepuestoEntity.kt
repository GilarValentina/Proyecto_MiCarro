package com.example.carro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

/** Repuesto asociado a un mantenimiento (RF-24 a RF-27). */
@Entity(
    tableName = "repuestos",
    foreignKeys = [
        ForeignKey(
            entity = MantenimientoEntity::class,
            parentColumns = ["id"],
            childColumns = ["mantenimientoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("mantenimientoId")]
)
data class RepuestoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mantenimientoId: Long,
    val nombre: String,
    val marca: String? = null,
    val referencia: String? = null,
    val cantidad: Int = 1,
    val valorUnitario: Double = 0.0,
    val proveedor: String? = null,
    val fechaInstalacion: LocalDate? = null,
    val garantiaDias: Int? = null,
    val garantiaKm: Long? = null,
    val observaciones: String? = null,
    val instaladoActualmente: Boolean = true
)
