package com.example.carro.domain.plan

import com.example.carro.domain.model.EstadoActividad
import java.time.LocalDate

/**
 * Calcula el estado de una actividad de mantenimiento (RF-14).
 *
 * Reglas de negocio:
 * - RN-01: una actividad está **vencida** si supera cualquiera de sus límites activos (fecha o km).
 * - RN-02: una actividad está **próxima** si entra en el margen de anticipación configurado.
 * - Si no tiene ningún criterio de programación, queda **sin programación**.
 *
 * Es una función pura para facilitar las pruebas unitarias (RNF-13).
 */
object EstadoCalculator {

    fun calcular(
        actividad: Actividad,
        hoy: LocalDate,
        kilometrajeActual: Long
    ): EstadoActividad {
        if (!actividad.tieneProgramacion) return EstadoActividad.SIN_PROGRAMACION

        val vencidaPorFecha = actividad.programarPorFecha &&
            actividad.proximaFecha != null &&
            hoy.isAfter(actividad.proximaFecha)

        val vencidaPorKm = actividad.programarPorKm &&
            actividad.proximoKm != null &&
            kilometrajeActual > actividad.proximoKm

        // RN-01: vencida si supera cualquier límite activo.
        if (vencidaPorFecha || vencidaPorKm) return EstadoActividad.VENCIDA

        val proximaPorFecha = actividad.programarPorFecha &&
            actividad.proximaFecha != null &&
            !hoy.isBefore(actividad.proximaFecha.minusDays(actividad.anticipacionDias.toLong()))

        val proximaPorKm = actividad.programarPorKm &&
            actividad.proximoKm != null &&
            kilometrajeActual >= (actividad.proximoKm - actividad.anticipacionKm)

        // RN-02: próxima si entra en el margen de anticipación.
        if (proximaPorFecha || proximaPorKm) return EstadoActividad.PROXIMA

        return EstadoActividad.AL_DIA
    }
}
