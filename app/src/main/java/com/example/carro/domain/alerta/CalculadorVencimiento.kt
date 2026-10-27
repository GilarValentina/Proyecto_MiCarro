package com.example.carro.domain.alerta

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Cálculo puro del estado de vencimiento de un documento (RF-33, RN-01, RN-02).
 * - VENCIDO: la fecha de vencimiento ya pasó (RN-01).
 * - PROXIMO: faltan entre 0 y la anticipación configurada (RN-02).
 * - VIGENTE: aún falta más que la anticipación.
 */
object CalculadorVencimiento {

    fun diasRestantes(fechaVencimiento: LocalDate, hoy: LocalDate): Long =
        ChronoUnit.DAYS.between(hoy, fechaVencimiento)

    fun estado(fechaVencimiento: LocalDate, hoy: LocalDate, anticipacionDias: Int): EstadoVencimiento {
        val dias = diasRestantes(fechaVencimiento, hoy)
        return when {
            dias < 0 -> EstadoVencimiento.VENCIDO
            dias <= anticipacionDias -> EstadoVencimiento.PROXIMO
            else -> EstadoVencimiento.VIGENTE
        }
    }

    fun evaluar(documento: Documento, hoy: LocalDate, anticipacionDias: Int): DocumentoConEstado =
        DocumentoConEstado(
            documento = documento,
            estado = estado(documento.fechaVencimiento, hoy, anticipacionDias),
            diasRestantes = diasRestantes(documento.fechaVencimiento, hoy)
        )
}
