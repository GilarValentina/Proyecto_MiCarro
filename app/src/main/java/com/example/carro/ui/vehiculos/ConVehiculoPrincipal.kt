package com.example.carro.ui.vehiculos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carro.core.di.AppContainer
import com.example.carro.domain.vehiculo.Vehiculo
import kotlinx.coroutines.flow.map

private sealed interface EstadoPrincipal {
    data object Cargando : EstadoPrincipal
    data object SinVehiculo : EstadoPrincipal
    data class Listo(val vehiculo: Vehiculo) : EstadoPrincipal
}

/**
 * Resuelve el vehículo principal (RF-04) para los módulos que trabajan sobre un vehículo.
 * Muestra un indicador mientras carga y, si no hay vehículos, invita a registrar uno.
 */
@Composable
fun ConVehiculoPrincipal(
    container: AppContainer,
    mensajeSinVehiculo: String,
    onRegistrarVehiculo: () -> Unit,
    contenido: @Composable (Vehiculo) -> Unit
) {
    val flujo = remember(container) {
        container.vehiculoRepository.observarPrincipal().map { v ->
            if (v == null) EstadoPrincipal.SinVehiculo else EstadoPrincipal.Listo(v)
        }
    }
    val estado by flujo.collectAsStateWithLifecycle(initialValue = EstadoPrincipal.Cargando)

    when (val e = estado) {
        EstadoPrincipal.Cargando -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        EstadoPrincipal.SinVehiculo -> Box(Modifier.fillMaxSize().padding(24.dp), Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(mensajeSinVehiculo, textAlign = TextAlign.Center)
                Button(onClick = onRegistrarVehiculo) { Text("Registrar vehículo") }
            }
        }
        is EstadoPrincipal.Listo -> contenido(e.vehiculo)
    }
}
