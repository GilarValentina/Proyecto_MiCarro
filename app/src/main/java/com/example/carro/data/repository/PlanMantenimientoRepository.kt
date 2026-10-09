package com.example.carro.data.repository

import com.example.carro.data.dao.ActividadDao
import com.example.carro.data.dao.CategoriaDao
import com.example.carro.data.dao.VehiculoDao
import com.example.carro.data.entity.ActividadMantenimientoEntity
import com.example.carro.data.entity.CategoriaEntity
import com.example.carro.data.entity.VehiculoEntity
import com.example.carro.domain.model.EstadoActividad
import com.example.carro.domain.plan.Actividad
import com.example.carro.domain.plan.ActividadConEstado
import com.example.carro.domain.plan.CategoriasPredeterminadas
import com.example.carro.domain.plan.EstadoCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Repositorio del plan de mantenimiento (RF-10 a RF-16).
 * Traduce entre entidades Room y modelos de dominio, y calcula el estado de cada actividad.
 */
class PlanMantenimientoRepository(
    private val actividadDao: ActividadDao,
    private val categoriaDao: CategoriaDao,
    private val vehiculoDao: VehiculoDao
) {

    /** Vehículo con el que trabaja el plan; se integra con el módulo de vehículos (RF-04). */
    fun observarPrimerVehiculo(): Flow<VehiculoEntity?> =
        vehiculoDao.observarActivos().map { it.firstOrNull() }

    /** Crea un vehículo de prueba para poder usar el módulo antes de integrar vehículos. */
    suspend fun crearVehiculoDemo(): Long =
        vehiculoDao.insertar(
            VehiculoEntity(
                placa = "DEMO-" + (System.currentTimeMillis() % 1000),
                tipo = "AUTOMOVIL",
                marca = "Vehículo",
                linea = "de prueba",
                modelo = "2020",
                anio = 2020,
                kilometrajeActual = 50_000,
                esPrincipal = true
            )
        )

    fun observarActividades(
        vehiculoId: Long,
        kilometrajeActual: Long,
        hoy: LocalDate = LocalDate.now()
    ): Flow<List<ActividadConEstado>> =
        actividadDao.observarPorVehiculo(vehiculoId).map { lista ->
            lista.map { entity ->
                val actividad = entity.toDomain()
                ActividadConEstado(
                    actividad = actividad,
                    estado = EstadoCalculator.calcular(actividad, hoy, kilometrajeActual)
                )
            }
        }

    fun observarCategorias(): Flow<List<CategoriaEntity>> = categoriaDao.observarTodas()

    suspend fun obtenerActividad(id: Long): Actividad? =
        actividadDao.obtenerPorId(id)?.toDomain()

    suspend fun guardar(actividad: Actividad): Long {
        val entity = actividad.toEntity()
        return if (actividad.id == 0L) {
            actividadDao.insertar(entity)
        } else {
            actividadDao.actualizar(entity)
            actividad.id
        }
    }

    /** RF-15: pausar o reactivar una actividad. */
    suspend fun cambiarActiva(actividad: Actividad, activa: Boolean) {
        actividadDao.actualizar(actividad.copy(activa = activa).toEntity())
    }

    /** RF-15: eliminar una actividad. */
    suspend fun eliminar(actividad: Actividad) {
        actividadDao.eliminar(actividad.toEntity())
    }

    /** RF-12: siembra las categorías iniciales si la tabla está vacía. */
    suspend fun sembrarCategoriasSiVacio() {
        if (categoriaDao.contar() == 0) {
            categoriaDao.insertarTodas(
                CategoriasPredeterminadas.nombres.map {
                    CategoriaEntity(nombre = it, esPredeterminada = true)
                }
            )
        }
    }
}

private fun ActividadMantenimientoEntity.toDomain(): Actividad = Actividad(
    id = id,
    vehiculoId = vehiculoId,
    nombre = nombre,
    categoriaId = categoriaId,
    descripcion = descripcion,
    programarPorFecha = programarPorFecha,
    programarPorKm = programarPorKm,
    intervaloDias = intervaloDias,
    intervaloKm = intervaloKm,
    proximaFecha = proximaFecha,
    proximoKm = proximoKm,
    anticipacionDias = anticipacionDias ?: 0,
    anticipacionKm = anticipacionKm ?: 0,
    activa = activa
)

private fun Actividad.toEntity(): ActividadMantenimientoEntity = ActividadMantenimientoEntity(
    id = id,
    vehiculoId = vehiculoId,
    nombre = nombre,
    categoriaId = categoriaId,
    descripcion = descripcion,
    programarPorFecha = programarPorFecha,
    programarPorKm = programarPorKm,
    intervaloDias = intervaloDias,
    intervaloKm = intervaloKm,
    proximaFecha = proximaFecha,
    proximoKm = proximoKm,
    anticipacionDias = anticipacionDias,
    anticipacionKm = anticipacionKm,
    estado = EstadoActividad.SIN_PROGRAMACION.name,
    activa = activa
)
