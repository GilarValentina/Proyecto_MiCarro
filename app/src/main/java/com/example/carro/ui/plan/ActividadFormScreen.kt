package com.example.carro.ui.plan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.carro.data.entity.CategoriaEntity
import com.example.carro.domain.plan.Actividad
import com.example.carro.domain.plan.PlantillaMantenimiento
import com.example.carro.domain.plan.PlantillasPredeterminadas
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Formulario de creación/edición de una actividad (RF-10, RF-11, RF-13, RF-16). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActividadFormScreen(
    viewModel: PlanMantenimientoViewModel,
    actividadId: Long?,
    onGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val editando = actividadId != null && actividadId != 0L

    var nombre by remember { mutableStateOf("") }
    var categoriaId by remember { mutableStateOf<Long?>(null) }
    var descripcion by remember { mutableStateOf("") }
    var porFecha by remember { mutableStateOf(false) }
    var porKm by remember { mutableStateOf(false) }
    var proximaFecha by remember { mutableStateOf("") }
    var proximoKm by remember { mutableStateOf("") }
    var anticipacionDias by remember { mutableStateOf("") }
    var anticipacionKm by remember { mutableStateOf("") }
    var errorFecha by remember { mutableStateOf(false) }

    LaunchedEffect(actividadId) {
        if (editando) {
            viewModel.obtenerActividad(actividadId!!)?.let { a ->
                nombre = a.nombre
                categoriaId = a.categoriaId
                descripcion = a.descripcion.orEmpty()
                porFecha = a.programarPorFecha
                porKm = a.programarPorKm
                proximaFecha = a.proximaFecha?.toString().orEmpty()
                proximoKm = a.proximoKm?.toString().orEmpty()
                anticipacionDias = a.anticipacionDias.takeIf { it > 0 }?.toString().orEmpty()
                anticipacionKm = a.anticipacionKm.takeIf { it > 0 }?.toString().orEmpty()
            }
        }
    }

    val formatter = remember { DateTimeFormatter.ISO_LOCAL_DATE }
    fun parseFecha(texto: String): LocalDate? =
        texto.takeIf { it.isNotBlank() }?.let {
            runCatching { LocalDate.parse(it, formatter) }.getOrNull()
        }

    val nombreValido = nombre.isNotBlank()
    val fechaValida = !porFecha || parseFecha(proximaFecha) != null
    val puedeGuardar = nombreValido && fechaValida

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (editando) "Editar actividad" else "Nueva actividad") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre *") },
                isError = nombre.isNotEmpty() && !nombreValido,
                modifier = Modifier.fillMaxWidth()
            )

            CategoriaSelector(
                categorias = state.categorias,
                seleccionada = categoriaId,
                onSeleccionar = { categoriaId = it }
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            if (!editando) {
                PlantillasRapidas(
                    categorias = state.categorias,
                    onAplicar = { plantilla, catId ->
                        nombre = plantilla.nombre
                        categoriaId = catId
                        plantilla.intervaloDias?.let {
                            porFecha = true
                            proximaFecha = LocalDate.now().plusDays(it.toLong()).toString()
                        }
                        plantilla.intervaloKm?.let {
                            porKm = true
                            proximoKm = it.toString()
                        }
                    }
                )
            }

            HorizontalDivider()
            Text("Programación", style = MaterialTheme.typography.titleSmall)

            SwitchRow("Programar por fecha", porFecha) { porFecha = it }
            if (porFecha) {
                OutlinedTextField(
                    value = proximaFecha,
                    onValueChange = { proximaFecha = it; errorFecha = false },
                    label = { Text("Próxima fecha (AAAA-MM-DD)") },
                    isError = proximaFecha.isNotEmpty() && parseFecha(proximaFecha) == null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = anticipacionDias,
                    onValueChange = { anticipacionDias = it.filter(Char::isDigit) },
                    label = { Text("Anticipación (días)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SwitchRow("Programar por kilometraje", porKm) { porKm = it }
            if (porKm) {
                OutlinedTextField(
                    value = proximoKm,
                    onValueChange = { proximoKm = it.filter(Char::isDigit) },
                    label = { Text("Próximo kilometraje") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = anticipacionKm,
                    onValueChange = { anticipacionKm = it.filter(Char::isDigit) },
                    label = { Text("Anticipación (km)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    viewModel.guardar(
                        Actividad(
                            id = if (editando) actividadId!! else 0L,
                            vehiculoId = 0L,
                            nombre = nombre.trim(),
                            categoriaId = categoriaId,
                            descripcion = descripcion.trim().ifBlank { null },
                            programarPorFecha = porFecha,
                            programarPorKm = porKm,
                            proximaFecha = if (porFecha) parseFecha(proximaFecha) else null,
                            proximoKm = if (porKm) proximoKm.toLongOrNull() else null,
                            anticipacionDias = anticipacionDias.toIntOrNull() ?: 0,
                            anticipacionKm = anticipacionKm.toLongOrNull() ?: 0
                        )
                    )
                    onGuardado()
                },
                enabled = puedeGuardar,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Guardar") }
        }
    }
}

@Composable
private fun SwitchRow(texto: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(texto)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriaSelector(
    categorias: List<CategoriaEntity>,
    seleccionada: Long?,
    onSeleccionar: (Long?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val texto = categorias.firstOrNull { it.id == seleccionada }?.nombre ?: "Sin categoría"

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = texto,
            onValueChange = {},
            readOnly = true,
            label = { Text("Categoría") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("Sin categoría") },
                onClick = { onSeleccionar(null); expanded = false }
            )
            categorias.forEach { cat ->
                DropdownMenuItem(
                    text = { Text(cat.nombre) },
                    onClick = { onSeleccionar(cat.id); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun PlantillasRapidas(
    categorias: List<CategoriaEntity>,
    onAplicar: (PlantillaMantenimiento, Long?) -> Unit
) {
    Text("Plantillas rápidas", style = MaterialTheme.typography.labelLarge)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlantillasPredeterminadas.lista.forEach { plantilla ->
            FilterChip(
                selected = false,
                onClick = {
                    val catId = categorias.firstOrNull { it.nombre == plantilla.categoria }?.id
                    onAplicar(plantilla, catId)
                },
                label = { Text(plantilla.nombre) }
            )
        }
    }
}
