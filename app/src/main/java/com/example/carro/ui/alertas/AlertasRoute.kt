package com.example.carro.ui.alertas

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.carro.core.alertas.AlertasWorker
import com.example.carro.core.di.AppContainer
import kotlinx.coroutines.launch

private sealed interface PantallaAlertas {
    data object Principal : PantallaAlertas
    data class Formulario(val documentoId: Long?) : PantallaAlertas
}

/**
 * Punto de entrada del módulo de alertas y documentos (RF-28 a RF-33).
 * Resuelve el vehículo activo y solicita el permiso de notificaciones (Android 13+).
 */
@Composable
fun AlertasRoute(
    container: AppContainer,
    onVolver: () -> Unit
) {
    val repository = container.alertasRepository
    val context = LocalContext.current
    val vehiculo by repository.observarPrimerVehiculo().collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val v = vehiculo
    if (v == null) {
        Box(Modifier.fillMaxSize().padding(24.dp), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Las alertas y documentos necesitan un vehículo registrado.")
                Button(onClick = { scope.launch { repository.crearVehiculoDemo() } }) {
                    Text("Crear vehículo de prueba")
                }
            }
        }
        return
    }

    val viewModel: AlertasViewModel = viewModel(
        key = "alertas_${v.id}",
        factory = AlertasViewModel.factory(container, v.id)
    )

    var pantalla by remember { mutableStateOf<PantallaAlertas>(PantallaAlertas.Principal) }

    when (val actual = pantalla) {
        is PantallaAlertas.Principal -> AlertasScreen(
            viewModel = viewModel,
            onCrearDocumento = { pantalla = PantallaAlertas.Formulario(null) },
            onEditarDocumento = { id -> pantalla = PantallaAlertas.Formulario(id) },
            onRevisarAhora = {
                val solicitud = OneTimeWorkRequestBuilder<AlertasWorker>().build()
                WorkManager.getInstance(context).enqueue(solicitud)
            },
            onVolver = onVolver
        )
        is PantallaAlertas.Formulario -> DocumentoFormScreen(
            viewModel = viewModel,
            documentoId = actual.documentoId,
            onGuardado = { pantalla = PantallaAlertas.Principal },
            onVolver = { pantalla = PantallaAlertas.Principal }
        )
    }
}
