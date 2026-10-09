package com.example.carro.domain.plan

import com.example.carro.domain.model.EstadoActividad
import java.time.LocalDate

/**
 * Representación de dominio de una actividad del plan de mantenimiento (RF-10 a RF-14).
 * Independiente de Room para mantener la separación de capas (RNF-12).
 */
data class Actividad(
    val id: Long = 0,
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
    val anticipacionDias: Int = 0,
    val anticipacionKm: Long = 0,
    val activa: Boolean = true
) {
    val tieneProgramacion: Boolean get() = programarPorFecha || programarPorKm
}

/** Estado calculado de una actividad junto con la actividad evaluada. */
data class ActividadConEstado(
    val actividad: Actividad,
    val estado: EstadoActividad
)
