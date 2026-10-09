package com.example.carro.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Pantalla de inicio provisional (RF-37). Lista los módulos del MVP.
 * Cada feature branch reemplazará las tarjetas por su contenido real.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavegar: (String) -> Unit = {}) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("MiCarro") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Control de mantenimiento vehicular",
                style = MaterialTheme.typography.titleMedium
            )
            val modulos = listOf(
                "Vehículos",
                "Kilometraje",
                "Plan de mantenimiento",
                "Mantenimientos y repuestos",
                "Alertas y documentos",
                "Historial y resumen"
            )
            modulos.forEach { modulo ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = modulo,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}
