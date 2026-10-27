package com.example.carro.ui.mantenimiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carro.domain.mantenimiento.MantenimientoResumen
import com.example.carro.domain.mantenimiento.Repuesto
import java.text.NumberFormat
import java.util.Locale

private val formatoMoneda: NumberFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

fun formatearMoneda(valor: Double): String = formatoMoneda.format(valor)

/** Pantalla de listado de mantenimientos y repuestos instalados (RF-17 a RF-27). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MantenimientoListScreen(
    viewModel: MantenimientoViewModel,
    onCrear: () -> Unit,
    onEditar: (Long) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensajeUsuario.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var pestana by remember { mutableIntStateOf(0) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.mensajeMostrado()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Mantenimientos") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (pestana == 0) {
                FloatingActionButton(onClick = onCrear) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo mantenimiento")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = pestana) {
                Tab(selected = pestana == 0, onClick = { pestana = 0 }, text = { Text("Historial") })
                Tab(selected = pestana == 1, onClick = { pestana = 1 }, text = { Text("Instalados") })
            }
            when {
                state.cargando -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                pestana == 0 -> HistorialTab(state.mantenimientos, onEditar) { viewModel.eliminar(it) }
                else -> InstaladosTab(state.instalados)
            }
        }
    }
}

@Composable
private fun HistorialTab(
    items: List<MantenimientoResumen>,
    onEditar: (Long) -> Unit,
    onEliminar: (MantenimientoResumen) -> Unit
) {
    if (items.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(24.dp), Alignment.Center) {
            Text("Aún no hay mantenimientos. Registra el primero con el botón +.")
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.mantenimiento.id }) { resumen ->
            val m = resumen.mantenimiento
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text(m.descripcion, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        AssistChip(onClick = {}, label = { Text(m.tipo.name.lowercase().replaceFirstChar { it.uppercase() }) })
                    }
                    Text("${m.fecha} · ${m.kilometraje} km", style = MaterialTheme.typography.bodySmall)
                    m.tallerResponsable?.takeIf { it.isNotBlank() }?.let {
                        Text("Taller: $it", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "Total: ${formatearMoneda(resumen.costoTotal)}  ·  ${resumen.cantidadRepuestos} repuesto(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Row(Modifier.fillMaxWidth(), Arrangement.End) {
                        IconButton(onClick = { onEditar(m.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = { onEliminar(resumen) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstaladosTab(instalados: List<Repuesto>) {
    if (instalados.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(24.dp), Alignment.Center) {
            Text("No hay repuestos marcados como instalados actualmente.")
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(instalados, key = { it.id }) { r ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(r.nombre, fontWeight = FontWeight.SemiBold)
                    val detalle = listOfNotNull(
                        r.marca?.takeIf { it.isNotBlank() },
                        r.referencia?.takeIf { it.isNotBlank() }?.let { "Ref. $it" },
                        "x${r.cantidad}"
                    ).joinToString(" · ")
                    if (detalle.isNotBlank()) Text(detalle, style = MaterialTheme.typography.bodySmall)
                    r.fechaInstalacion?.let {
                        Text("Instalado: $it", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
