package com.example.carro.ui.alertas

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.carro.core.alertas.AlertasWorker
import com.example.carro.core.di.AppContainer
import com.example.carro.ui.vehiculos.ConVehiculoPrincipal

private sealed interface PantallaAlertas {
    data object Principal : PantallaAlertas
    data class Formulario(val documentoId: Long?) : PantallaAlertas
}

/**
 * Punto de entrada del módulo de alertas y documentos (RF-28 a RF-33).
 * Trabaja sobre el vehículo principal (RF-04) y solicita el permiso de notificaciones (Android 13+).
 */
@Composable
fun AlertasRoute(
    container: AppContainer,
    onRegistrarVehiculo: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    val permisoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permisoLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    ConVehiculoPrincipal(
        container = container,
        mensajeSinVehiculo = "Las alertas y documentos necesitan un vehículo registrado.",
        onRegistrarVehiculo = onRegistrarVehiculo
    ) { v ->
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
}
