package com.example.carro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carro.MiCarroApplication
import com.example.carro.ui.home.HomeScreen
import com.example.carro.ui.mantenimiento.MantenimientoRoute

/**
 * Grafo de navegación raíz. El destino inicial es la pantalla de inicio;
 * cada feature branch agregará sus propios `composable(...)` para cada módulo.
 */
@Composable
fun MiCarroNavHost(
    navController: NavHostController = rememberNavController()
) {
    val container = (LocalContext.current.applicationContext as MiCarroApplication).container

    NavHost(
        navController = navController,
        startDestination = Destinos.INICIO
    ) {
        composable(Destinos.INICIO) {
            HomeScreen(onNavegar = { ruta -> navController.navigate(ruta) })
        }
        composable(Destinos.MANTENIMIENTOS) {
            MantenimientoRoute(
                container = container,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
