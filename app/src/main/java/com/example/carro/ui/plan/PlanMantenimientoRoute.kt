package com.example.carro.ui.plan

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

private sealed interface PantallaPlan {
    data object Lista : PantallaPlan
    data class Formulario(val actividadId: Long?) : PantallaPlan
}

/**
 * Punto de entrada del módulo de plan de mantenimiento.
 * Resuelve el vehículo activo (integración futura con el módulo de vehículos de Gilar)
 * y alterna entre el listado y el formulario.
 */
@Composable
fun PlanMantenimientoRoute(
    container: AppContainer,
    onVolver: () -> Unit
) {
    val repository = container.planMantenimientoRepository
    val vehiculo by repository.observarPrimerVehiculo().collectAsStateWithLifecycle(initialValue = null)
    val scope = rememberCoroutineScope()

    val v = vehiculo
    if (v == null) {
        SinVehiculo(onCrearDemo = { scope.launch { repository.crearVehiculoDemo() } })
        return
    }

    val viewModel: PlanMantenimientoViewModel = viewModel(
        key = "plan_${v.id}",
        factory = PlanMantenimientoViewModel.factory(container, v.id, v.kilometrajeActual)
    )

    var pantalla by remember { mutableStateOf<PantallaPlan>(PantallaPlan.Lista) }

    when (val actual = pantalla) {
        is PantallaPlan.Lista -> PlanMantenimientoScreen(
            viewModel = viewModel,
            onCrear = { pantalla = PantallaPlan.Formulario(null) },
            onEditar = { id -> pantalla = PantallaPlan.Formulario(id) }
        )

        is PantallaPlan.Formulario -> ActividadFormScreen(
            viewModel = viewModel,
            actividadId = actual.actividadId,
            onGuardado = { pantalla = PantallaPlan.Lista },
            onVolver = { pantalla = PantallaPlan.Lista }
        )
    }
}

@Composable
private fun SinVehiculo(onCrearDemo: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("El plan de mantenimiento necesita un vehículo registrado.")
            Button(onClick = onCrearDemo) { Text("Crear vehículo de prueba") }
        }
    }
}
