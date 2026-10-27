package com.example.carro.ui.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.carro.core.di.AppContainer
import com.example.carro.data.repository.AlertasRepository
import com.example.carro.domain.alerta.Alerta
import com.example.carro.domain.alerta.ConfiguracionAlertas
import com.example.carro.domain.alerta.Documento
import com.example.carro.domain.alerta.DocumentoConEstado
import com.example.carro.domain.alerta.DocumentoValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado de UI del módulo de alertas y documentos (RF-28 a RF-33). */
data class AlertasUiState(
    val documentos: List<DocumentoConEstado> = emptyList(),
    val alertas: List<Alerta> = emptyList(),
    val configuracion: ConfiguracionAlertas = ConfiguracionAlertas(),
    val cargando: Boolean = true
)

class AlertasViewModel(
    private val repository: AlertasRepository,
    private val vehiculoId: Long
) : ViewModel() {

    private val mensaje = MutableStateFlow<String?>(null)
    val mensajeUsuario: StateFlow<String?> = mensaje.asStateFlow()

    val uiState: StateFlow<AlertasUiState> =
        combine(
            repository.observarDocumentos(vehiculoId),
            repository.observarAlertasActivas(),
            repository.configuracion
        ) { documentos, alertas, config ->
            AlertasUiState(
                documentos = documentos,
                alertas = alertas,
                configuracion = config,
                cargando = false
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AlertasUiState()
        )

    suspend fun obtenerDocumento(id: Long): Documento? = repository.obtenerDocumento(id)

    fun validar(documento: Documento): DocumentoValidator.Resultado =
        DocumentoValidator.validar(documento.copy(vehiculoId = vehiculoId))

    fun guardarDocumento(documento: Documento) {
        viewModelScope.launch {
            repository.guardarDocumento(documento.copy(vehiculoId = vehiculoId))
            mensaje.value = "Documento guardado"
        }
    }

    fun eliminarDocumento(documento: Documento) {
        viewModelScope.launch {
            repository.eliminarDocumento(documento)
            mensaje.value = "Documento eliminado"
        }
    }

    /** RF-31: pospone el aviso sin marcar el documento como atendido (RN-08). */
    fun posponerAlerta(alerta: Alerta, dias: Int) {
        viewModelScope.launch {
            repository.posponerAlerta(alerta, dias)
            mensaje.value = "Alerta pospuesta $dias día(s)"
        }
    }

    fun marcarAtendida(alerta: Alerta) {
        viewModelScope.launch {
            repository.marcarAtendida(alerta)
            mensaje.value = "Alerta marcada como atendida"
        }
    }

    /** RF-29: activa/desactiva alertas y ajusta la anticipación. */
    fun actualizarConfiguracion(config: ConfiguracionAlertas) {
        repository.actualizarConfiguracion(config)
    }

    fun mensajeMostrado() { mensaje.value = null }

    companion object {
        fun factory(container: AppContainer, vehiculoId: Long): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
                    AlertasViewModel(container.alertasRepository, vehiculoId) as T
            }
    }
}
