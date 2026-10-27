package com.example.carro.ui.mantenimiento

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.carro.core.di.AppContainer
import com.example.carro.data.repository.MantenimientoRepository
import com.example.carro.domain.mantenimiento.Mantenimiento
import com.example.carro.domain.mantenimiento.MantenimientoResumen
import com.example.carro.domain.mantenimiento.MantenimientoValidator
import com.example.carro.domain.mantenimiento.Repuesto
import com.example.carro.domain.mantenimiento.ResultadoValidacion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Estado de UI de la lista de mantenimientos (RF-17 a RF-27). */
data class MantenimientoUiState(
    val mantenimientos: List<MantenimientoResumen> = emptyList(),
    val instalados: List<Repuesto> = emptyList(),
    val cargando: Boolean = true
)

class MantenimientoViewModel(
    private val repository: MantenimientoRepository,
    private val vehiculoId: Long
) : ViewModel() {

    private val mensaje = MutableStateFlow<String?>(null)
    val mensajeUsuario: StateFlow<String?> = mensaje.asStateFlow()

    val uiState: StateFlow<MantenimientoUiState> =
        kotlinx.coroutines.flow.combine(
            repository.observarMantenimientos(vehiculoId),
            repository.observarInstalados()
        ) { mantenimientos, instalados ->
            MantenimientoUiState(mantenimientos = mantenimientos, instalados = instalados, cargando = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MantenimientoUiState()
        )

    /** Valida sin persistir (los errores bloquean, las advertencias se confirman). */
    suspend fun validar(mantenimiento: Mantenimiento): ResultadoValidacion =
        MantenimientoValidator.validar(
            mantenimiento = mantenimiento.copy(vehiculoId = vehiculoId),
            hoy = LocalDate.now(),
            ultimaLecturaKm = repository.ultimaLecturaKm(vehiculoId)
        )

    fun guardar(mantenimiento: Mantenimiento) {
        viewModelScope.launch {
            repository.guardar(mantenimiento.copy(vehiculoId = vehiculoId))
            mensaje.value = "Mantenimiento guardado"
        }
    }

    fun eliminar(resumen: MantenimientoResumen) {
        viewModelScope.launch {
            repository.eliminar(resumen.mantenimiento)
            mensaje.value = "Mantenimiento eliminado"
        }
    }

    suspend fun obtenerMantenimiento(id: Long): Mantenimiento? = repository.obtenerMantenimiento(id)

    fun mensajeMostrado() { mensaje.value = null }

    companion object {
        fun factory(container: AppContainer, vehiculoId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                    MantenimientoViewModel(container.mantenimientoRepository, vehiculoId) as T
            }
    }
}
