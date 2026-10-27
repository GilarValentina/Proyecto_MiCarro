package com.example.carro.domain.mantenimiento

import com.example.carro.domain.model.TipoMantenimiento
import java.time.LocalDate

/**
 * Repuesto instalado en un mantenimiento (RF-24 a RF-27).
 * Modelo de dominio independiente de Room (RNF-12).
 */
data class Repuesto(
    val id: Long = 0,
    val mantenimientoId: Long = 0,
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
) {
    /** Subtotal del repuesto (cantidad x valor unitario). */
    val subtotal: Double get() = cantidad * valorUnitario
}

/**
 * Mantenimiento realizado, preventivo o correctivo (RF-17 a RF-23).
 * El costo total se deriva de la mano de obra más los repuestos (RN-04).
 */
data class Mantenimiento(
    val id: Long = 0,
    val vehiculoId: Long,
    val actividadId: Long? = null,
    val fecha: LocalDate,
    val kilometraje: Long,
    val tipo: TipoMantenimiento,
    val descripcion: String,
    val tallerResponsable: String? = null,
    val costoManoObra: Double = 0.0,
    val repuestos: List<Repuesto> = emptyList()
) {
    /** RN-04: suma del valor de todos los repuestos. */
    val costoRepuestos: Double get() = repuestos.sumOf { it.subtotal }

    /** RN-04: total = mano de obra + repuestos. */
    val costoTotal: Double get() = costoManoObra + costoRepuestos
}

/** Vista de lista de un mantenimiento con el conteo de repuestos. */
data class MantenimientoResumen(
    val mantenimiento: Mantenimiento,
    val cantidadRepuestos: Int,
    val costoTotal: Double
)
