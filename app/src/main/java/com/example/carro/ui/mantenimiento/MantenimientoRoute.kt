package com.example.carro.ui.mantenimiento

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carro.core.di.AppContainer
import com.example.carro.ui.vehiculos.ConVehiculoPrincipal

private sealed interface PantallaMantenimiento {
    data object Lista : PantallaMantenimiento
    data class Formulario(val id: Long?) : PantallaMantenimiento
}

/**
 * Punto de entrada del módulo de mantenimientos y repuestos.
 * Trabaja sobre el vehículo principal (RF-04) y alterna entre lista y formulario.
 */
@Composable
fun MantenimientoRoute(
    container: AppContainer,
    onRegistrarVehiculo: () -> Unit,
    onVolver: () -> Unit
) {
    ConVehiculoPrincipal(
        container = container,
        mensajeSinVehiculo = "El registro de mantenimientos necesita un vehículo.",
        onRegistrarVehiculo = onRegistrarVehiculo
    ) { v ->
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
}
