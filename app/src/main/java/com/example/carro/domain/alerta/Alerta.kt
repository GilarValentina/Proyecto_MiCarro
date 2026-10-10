package com.example.carro.domain.alerta

import java.time.LocalDate

/** Tipo de origen de una alerta local (RF-28, RF-33). */
enum class TipoAlerta { MANTENIMIENTO, DOCUMENTO }

/**
 * Alerta local mostrada al usuario (RF-28, RF-30, RF-31).
 * Modelo de dominio independiente de Room (RNF-12).
 */
data class Alerta(
    val id: Long = 0,
    val vehiculoId: Long,
    val documentoId: Long? = null,
    val actividadId: Long? = null,
    val tipo: TipoAlerta,
    val causa: String,
    val fechaAviso: LocalDate,
    val pospuestaHasta: LocalDate? = null,
    val activa: Boolean = true,
    val atendida: Boolean = false
) {
    /** Fecha efectiva en la que debe mostrarse el aviso (RN-08). */
    val fechaEfectiva: LocalDate get() = pospuestaHasta ?: fechaAviso
}

/**
 * Configuración de alertas del usuario (RF-29).
 * La anticipación define cuántos días antes del vencimiento se considera "próximo" (RN-02).
 */
data class ConfiguracionAlertas(
    val alertasActivas: Boolean = true,
    val anticipacionDias: Int = 15
) {
    companion object {
        const val ANTICIPACION_MINIMA = 1
        const val ANTICIPACION_MAXIMA = 90
    }
}
