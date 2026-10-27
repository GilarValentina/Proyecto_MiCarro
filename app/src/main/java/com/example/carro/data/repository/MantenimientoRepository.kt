package com.example.carro.data.repository

import androidx.room.withTransaction
import com.example.carro.core.database.MiCarroDatabase
import com.example.carro.data.dao.KilometrajeDao
import com.example.carro.data.dao.MantenimientoDao
import com.example.carro.data.dao.RepuestoDao
import com.example.carro.data.dao.VehiculoDao
import com.example.carro.data.entity.MantenimientoEntity
import com.example.carro.data.entity.RepuestoEntity
import com.example.carro.data.entity.VehiculoEntity
import com.example.carro.domain.mantenimiento.Mantenimiento
import com.example.carro.domain.mantenimiento.MantenimientoResumen
import com.example.carro.domain.mantenimiento.Repuesto
import com.example.carro.domain.model.TipoMantenimiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repositorio del registro de mantenimientos y repuestos (RF-17 a RF-27).
 * Guarda el mantenimiento y sus repuestos de forma atómica y calcula los costos (RN-04).
 */
class MantenimientoRepository(
    private val database: MiCarroDatabase,
    private val mantenimientoDao: MantenimientoDao,
    private val repuestoDao: RepuestoDao,
    private val kilometrajeDao: KilometrajeDao,
    private val vehiculoDao: VehiculoDao
) {

    fun observarMantenimientos(vehiculoId: Long): Flow<List<MantenimientoResumen>> =
        mantenimientoDao.observarPorVehiculo(vehiculoId).map { lista ->
            lista.map { entity ->
                MantenimientoResumen(
                    mantenimiento = entity.toDomain(emptyList()),
                    cantidadRepuestos = repuestoDao.contarPorMantenimiento(entity.id),
                    costoTotal = entity.costoTotal
                )
            }
        }

    /** RF-27: repuestos instalados actualmente, en todos los vehículos. */
    fun observarInstalados(): Flow<List<Repuesto>> =
        repuestoDao.observarInstalados().map { lista -> lista.map { it.toDomain() } }

    suspend fun obtenerMantenimiento(id: Long): Mantenimiento? {
        val entity = mantenimientoDao.obtenerPorId(id) ?: return null
        val repuestos = repuestoDao.obtenerPorMantenimiento(id).map { it.toDomain() }
        return entity.toDomain(repuestos)
    }

    /** RN-06: última lectura de kilometraje conocida del vehículo. */
    suspend fun ultimaLecturaKm(vehiculoId: Long): Long? {
        val maxLectura = kilometrajeDao.obtenerMaximoOdometro(vehiculoId)
        val actual = vehiculoDao.obtenerPorId(vehiculoId)?.kilometrajeActual
        return listOfNotNull(maxLectura, actual).maxOrNull()
    }

    /**
     * Guarda el mantenimiento y reemplaza su lista de repuestos de forma atómica (RF-17 a RF-27).
     * Devuelve el id del mantenimiento.
     */
    suspend fun guardar(mantenimiento: Mantenimiento): Long = database.withTransaction {
        val entity = mantenimiento.toEntity()
        val mantenimientoId = if (mantenimiento.id == 0L) {
            mantenimientoDao.insertar(entity)
        } else {
            mantenimientoDao.actualizar(entity)
            repuestoDao.eliminarPorMantenimiento(mantenimiento.id)
            mantenimiento.id
        }
        mantenimiento.repuestos.forEach { repuesto ->
            repuestoDao.insertar(repuesto.toEntity(mantenimientoId))
        }
        mantenimientoId
    }

    /** RF-22: eliminar un mantenimiento (sus repuestos se borran en cascada). */
    suspend fun eliminar(mantenimiento: Mantenimiento) {
        mantenimientoDao.eliminar(mantenimiento.toEntity())
    }

    fun observarPrimerVehiculo(): Flow<VehiculoEntity?> =
        vehiculoDao.observarActivos().map { it.firstOrNull() }

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
}

private fun MantenimientoEntity.toDomain(repuestos: List<Repuesto>): Mantenimiento = Mantenimiento(
    id = id,
    vehiculoId = vehiculoId,
    actividadId = actividadId,
    fecha = fecha,
    kilometraje = kilometraje,
    tipo = runCatching { TipoMantenimiento.valueOf(tipo) }.getOrDefault(TipoMantenimiento.PREVENTIVO),
    descripcion = descripcion,
    tallerResponsable = tallerResponsable,
    costoManoObra = costoManoObra,
    repuestos = repuestos
)

private fun Mantenimiento.toEntity(): MantenimientoEntity = MantenimientoEntity(
    id = id,
    vehiculoId = vehiculoId,
    actividadId = actividadId,
    fecha = fecha,
    kilometraje = kilometraje,
    tipo = tipo.name,
    descripcion = descripcion,
    tallerResponsable = tallerResponsable,
    costoManoObra = costoManoObra,
    costoRepuestos = costoRepuestos,
    costoTotal = costoTotal
)

private fun RepuestoEntity.toDomain(): Repuesto = Repuesto(
    id = id,
    mantenimientoId = mantenimientoId,
    nombre = nombre,
    marca = marca,
    referencia = referencia,
    cantidad = cantidad,
    valorUnitario = valorUnitario,
    proveedor = proveedor,
    fechaInstalacion = fechaInstalacion,
    garantiaDias = garantiaDias,
    garantiaKm = garantiaKm,
    observaciones = observaciones,
    instaladoActualmente = instaladoActualmente
)

private fun Repuesto.toEntity(mantenimientoId: Long): RepuestoEntity = RepuestoEntity(
    id = if (id == 0L) 0 else id,
    mantenimientoId = mantenimientoId,
    nombre = nombre,
    marca = marca,
    referencia = referencia,
    cantidad = cantidad,
    valorUnitario = valorUnitario,
    proveedor = proveedor,
    fechaInstalacion = fechaInstalacion,
    garantiaDias = garantiaDias,
    garantiaKm = garantiaKm,
    observaciones = observaciones,
    instaladoActualmente = instaladoActualmente
)
