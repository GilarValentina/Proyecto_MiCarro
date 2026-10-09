package com.example.carro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carro.ui.home.HomeScreen

/**
 * Grafo de navegación raíz. El destino inicial es la pantalla de inicio;
 * cada feature branch agregará sus propios `composable(...)` para cada módulo.
 */
@Composable
fun MiCarroNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Destinos.INICIO
    ) {
        composable(Destinos.INICIO) {
            HomeScreen(onNavegar = { ruta -> navController.navigate(ruta) })
        }
    }
}
