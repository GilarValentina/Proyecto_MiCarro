package com.example.carro.domain.mantenimiento

import java.time.LocalDate

/** Resultado de validar un mantenimiento antes de persistirlo. */
data class ResultadoValidacion(
    val errores: List<String> = emptyList(),
    val advertencias: List<String> = emptyList()
) {
    val esValido: Boolean get() = errores.isEmpty()
}

/**
 * Validaciones del registro de mantenimiento (RNF-16, RN-05, RN-06).
 * Funciones puras para facilitar las pruebas unitarias (RNF-13).
 *
 * - Los errores impiden guardar.
 * - Las advertencias se pueden confirmar (p. ej. kilometraje menor a la última lectura, RN-06).
 */
object MantenimientoValidator {

    fun validar(
        mantenimiento: Mantenimiento,
        hoy: LocalDate,
        ultimaLecturaKm: Long?
    ): ResultadoValidacion {
        val errores = mutableListOf<String>()
        val advertencias = mutableListOf<String>()

        if (mantenimiento.descripcion.isBlank()) {
            errores += "La descripción es obligatoria."
        }

        // RN-05: la fecha de realización no puede ser futura.
        if (mantenimiento.fecha.isAfter(hoy)) {
            errores += "La fecha no puede ser futura."
        }

        // RNF-16: montos no negativos.
        if (mantenimiento.kilometraje < 0) {
            errores += "El kilometraje no puede ser negativo."
        }
        if (mantenimiento.costoManoObra < 0) {
            errores += "El costo de mano de obra no puede ser negativo."
        }
        mantenimiento.repuestos.forEach { r ->
            if (r.nombre.isBlank()) errores += "Todos los repuestos deben tener nombre."
            if (r.cantidad <= 0) errores += "La cantidad de '${r.nombre}' debe ser mayor a cero."
            if (r.valorUnitario < 0) errores += "El valor de '${r.nombre}' no puede ser negativo."
        }

        // RN-06: kilometraje menor a la última lectura requiere confirmación.
        if (ultimaLecturaKm != null && mantenimiento.kilometraje < ultimaLecturaKm) {
            advertencias += "El kilometraje ($${mantenimiento.kilometraje}) es menor que la última lectura ($ultimaLecturaKm)."
        }

        return ResultadoValidacion(errores, advertencias)
    }
}
