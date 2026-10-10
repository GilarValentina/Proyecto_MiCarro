package com.example.carro.ui.mantenimiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carro.core.di.AppContainer
import kotlinx.coroutines.launch

private sealed interface PantallaMantenimiento {
    data object Lista : PantallaMantenimiento
    data class Formulario(val id: Long?) : PantallaMantenimiento
}

/**
 * Punto de entrada del módulo de mantenimientos y repuestos.
 * Resuelve el vehículo activo (integración futura con el módulo de vehículos)
 * y alterna entre lista y formulario.
 */
@Composable
fun MantenimientoRoute(
    container: AppContainer,
    onVolver: () -> Unit
) {
    val repository = container.mantenimientoRepository
    val vehiculo by repository.observarPrimerVehiculo().collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    val v = vehiculo
    if (v == null) {
        Box(Modifier.fillMaxSize().padding(24.dp), Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("El registro de mantenimientos necesita un vehículo.")
                Button(onClick = { scope.launch { repository.crearVehiculoDemo() } }) {
                    Text("Crear vehículo de prueba")
                }
            }
        }
        return
    }

    val viewModel: MantenimientoViewModel = viewModel(
        key = "mant_${v.id}",
        factory = MantenimientoViewModel.factory(container, v.id)
    )

    var pantalla by remember { mutableStateOf<PantallaMantenimiento>(PantallaMantenimiento.Lista) }

    when (val actual = pantalla) {
        is PantallaMantenimiento.Lista -> MantenimientoListScreen(
            viewModel = viewModel,
            onCrear = { pantalla = PantallaMantenimiento.Formulario(null) },
            onEditar = { id -> pantalla = PantallaMantenimiento.Formulario(id) }
        )
        is PantallaMantenimiento.Formulario -> MantenimientoFormScreen(
            viewModel = viewModel,
            mantenimientoId = actual.id,
            onGuardado = { pantalla = PantallaMantenimiento.Lista },
            onVolver = { pantalla = PantallaMantenimiento.Lista }
        )
    }
}
