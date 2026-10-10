package com.example.carro.domain.alerta

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Pruebas del cálculo de estado de vencimiento de documentos (RF-33, RN-01, RN-02).
 */
class CalculadorVencimientoTest {

    private val hoy = LocalDate.of(2026, 1, 15)
    private val anticipacion = 15

    @Test
    fun `documento con vencimiento lejano esta vigente`() {
        val estado = CalculadorVencimiento.estado(hoy.plusDays(40), hoy, anticipacion)
        assertEquals(EstadoVencimiento.VIGENTE, estado)
    }

    @Test
    fun `documento dentro de la anticipacion esta proximo (RN-02)`() {
        val estado = CalculadorVencimiento.estado(hoy.plusDays(10), hoy, anticipacion)
        assertEquals(EstadoVencimiento.PROXIMO, estado)
    }

    @Test
    fun `documento en el limite de la anticipacion esta proximo`() {
        val estado = CalculadorVencimiento.estado(hoy.plusDays(15), hoy, anticipacion)
        assertEquals(EstadoVencimiento.PROXIMO, estado)
    }

    @Test
    fun `documento que vence hoy esta proximo`() {
        val estado = CalculadorVencimiento.estado(hoy, hoy, anticipacion)
        assertEquals(EstadoVencimiento.PROXIMO, estado)
    }

    @Test
    fun `documento con fecha pasada esta vencido (RN-01)`() {
        val estado = CalculadorVencimiento.estado(hoy.minusDays(1), hoy, anticipacion)
        assertEquals(EstadoVencimiento.VENCIDO, estado)
    }

    @Test
    fun `dias restantes se calculan correctamente`() {
        assertEquals(10L, CalculadorVencimiento.diasRestantes(hoy.plusDays(10), hoy))
        assertEquals(-3L, CalculadorVencimiento.diasRestantes(hoy.minusDays(3), hoy))
    }

    @Test
    fun `evaluar combina estado y dias restantes`() {
        val documento = Documento(
            vehiculoId = 1,
            tipo = TipoDocumento.SOAT,
            fechaVencimiento = hoy.plusDays(5)
        )
        val resultado = CalculadorVencimiento.evaluar(documento, hoy, anticipacion)
        assertEquals(EstadoVencimiento.PROXIMO, resultado.estado)
        assertEquals(5L, resultado.diasRestantes)
    }
}
