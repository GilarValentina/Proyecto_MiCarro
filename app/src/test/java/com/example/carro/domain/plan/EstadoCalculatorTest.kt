package com.example.carro.domain.plan

import com.example.carro.domain.model.EstadoActividad
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Pruebas del cálculo de estado de actividades (RNF-13).
 * Cubre las reglas RN-01 (vencida) y RN-02 (próxima) y RF-14 (estados).
 */
class EstadoCalculatorTest {

    private val hoy = LocalDate.of(2026, 1, 15)

    private fun actividad(
        porFecha: Boolean = false,
        porKm: Boolean = false,
        proximaFecha: LocalDate? = null,
        proximoKm: Long? = null,
        anticipacionDias: Int = 0,
        anticipacionKm: Long = 0
    ) = Actividad(
        vehiculoId = 1,
        nombre = "Cambio de aceite",
        programarPorFecha = porFecha,
        programarPorKm = porKm,
        proximaFecha = proximaFecha,
        proximoKm = proximoKm,
        anticipacionDias = anticipacionDias,
        anticipacionKm = anticipacionKm
    )

    @Test
    fun `sin programacion cuando no hay criterios`() {
        val estado = EstadoCalculator.calcular(actividad(), hoy, 10_000)
        assertEquals(EstadoActividad.SIN_PROGRAMACION, estado)
    }

    @Test
    fun `vencida por fecha superada (RN-01)`() {
        val a = actividad(porFecha = true, proximaFecha = hoy.minusDays(1))
        assertEquals(EstadoActividad.VENCIDA, EstadoCalculator.calcular(a, hoy, 0))
    }

    @Test
    fun `vencida por kilometraje superado (RN-01)`() {
        val a = actividad(porKm = true, proximoKm = 9_000)
        assertEquals(EstadoActividad.VENCIDA, EstadoCalculator.calcular(a, hoy, 10_000))
    }

    @Test
    fun `proxima dentro del margen de dias (RN-02)`() {
        val a = actividad(porFecha = true, proximaFecha = hoy.plusDays(5), anticipacionDias = 10)
        assertEquals(EstadoActividad.PROXIMA, EstadoCalculator.calcular(a, hoy, 0))
    }

    @Test
    fun `proxima dentro del margen de km (RN-02)`() {
        val a = actividad(porKm = true, proximoKm = 10_000, anticipacionKm = 500)
        assertEquals(EstadoActividad.PROXIMA, EstadoCalculator.calcular(a, hoy, 9_600))
    }

    @Test
    fun `al dia cuando aun falta y esta fuera del margen`() {
        val a = actividad(porFecha = true, proximaFecha = hoy.plusDays(60), anticipacionDias = 10)
        assertEquals(EstadoActividad.AL_DIA, EstadoCalculator.calcular(a, hoy, 0))
    }

    @Test
    fun `vence tiene prioridad sobre proxima con doble criterio`() {
        val a = actividad(
            porFecha = true,
            porKm = true,
            proximaFecha = hoy.plusDays(5),
            anticipacionDias = 10,
            proximoKm = 9_000
        )
        assertEquals(EstadoActividad.VENCIDA, EstadoCalculator.calcular(a, hoy, 10_000))
    }
}
