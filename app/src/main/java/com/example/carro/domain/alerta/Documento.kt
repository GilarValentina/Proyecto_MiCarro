package com.example.carro.domain.alerta

import java.time.LocalDate

/**
 * Tipo de documento legal con vencimiento (RF-32).
 */
enum class TipoDocumento(val etiqueta: String) {
    SOAT("SOAT"),
    REVISION_TECNOMECANICA("Revisión técnico-mecánica"),
    SEGURO("Seguro"),
    OTRO("Otro");

    companion object {
        fun desde(valor: String): TipoDocumento =
            entries.firstOrNull { it.name == valor } ?: OTRO
    }
}

/**
 * Documento legal del vehículo con fecha de vencimiento (RF-32, RF-33).
 * Modelo de dominio independiente de Room (RNF-12).
 */
data class Documento(
    val id: Long = 0,
    val vehiculoId: Long,
    val tipo: TipoDocumento,
    val nombre: String? = null,
    val fechaVencimiento: LocalDate
) {
    /** Nombre a mostrar: el personalizado o la etiqueta del tipo. */
    val titulo: String get() = nombre?.takeIf { it.isNotBlank() } ?: tipo.etiqueta
}

/** Estado de vencimiento de un documento o actividad (RN-01, RN-02, RF-33). */
enum class EstadoVencimiento { VIGENTE, PROXIMO, VENCIDO }

/** Documento junto con su estado calculado y los días restantes para el vencimiento. */
data class DocumentoConEstado(
    val documento: Documento,
    val estado: EstadoVencimiento,
    val diasRestantes: Long
)
