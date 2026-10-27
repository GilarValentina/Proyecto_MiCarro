package com.example.carro.domain.mantenimiento

import com.example.carro.domain.model.TipoMantenimiento
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * Pruebas de validación y cálculo de costos del mantenimiento (RNF-13).
 * Cubre RN-04 (total), RN-05 (fecha futura), RN-06 (km menor) y RNF-16 (montos).
 */
class MantenimientoValidatorTest {

    private val hoy = LocalDate.of(2026, 1, 15)

    private fun base(
        fecha: LocalDate = hoy,
        kilometraje: Long = 10_000,
        manoObra: Double = 0.0,
        repuestos: List<Repuesto> = emptyList(),
        descripcion: String = "Cambio de aceite"
    ) = Mantenimiento(
        vehiculoId = 1,
        fecha = fecha,
        kilometraje = kilometraje,
        tipo = TipoMantenimiento.PREVENTIVO,
        descripcion = descripcion,
        costoManoObra = manoObra,
        repuestos = repuestos
    )

    @Test
    fun `costo total es mano de obra mas repuestos (RN-04)`() {
        val m = base(
            manoObra = 50_000.0,
            repuestos = listOf(
                Repuesto(nombre = "Filtro", cantidad = 2, valorUnitario = 15_000.0),
                Repuesto(nombre = "Aceite", cantidad = 1, valorUnitario = 40_000.0)
            )
        )
        assertEquals(70_000.0, m.costoRepuestos, 0.001)
        assertEquals(120_000.0, m.costoTotal, 0.001)
    }

    @Test
    fun `fecha futura es invalida (RN-05)`() {
        val r = MantenimientoValidator.validar(base(fecha = hoy.plusDays(1)), hoy, null)
        assertFalse(r.esValido)
    }

    @Test
    fun `kilometraje menor a ultima lectura genera advertencia (RN-06)`() {
        val r = MantenimientoValidator.validar(base(kilometraje = 9_000), hoy, ultimaLecturaKm = 12_000)
        assertTrue(r.esValido)
        assertTrue(r.advertencias.isNotEmpty())
    }

    @Test
    fun `montos negativos son invalidos (RNF-16)`() {
        val r = MantenimientoValidator.validar(base(manoObra = -1.0), hoy, null)
        assertFalse(r.esValido)
    }

    @Test
    fun `descripcion vacia es invalida`() {
        val r = MantenimientoValidator.validar(base(descripcion = "  "), hoy, null)
        assertFalse(r.esValido)
    }

    @Test
    fun `mantenimiento valido sin advertencias`() {
        val r = MantenimientoValidator.validar(base(kilometraje = 15_000), hoy, ultimaLecturaKm = 12_000)
        assertTrue(r.esValido)
        assertTrue(r.advertencias.isEmpty())
    }
}
