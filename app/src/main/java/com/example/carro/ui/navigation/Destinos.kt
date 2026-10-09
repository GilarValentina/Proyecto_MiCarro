package com.example.carro.ui.navigation

/** Rutas de navegación de la app. Cada módulo registra su pantalla en el NavHost. */
object Destinos {
    const val INICIO = "inicio"

    // Módulos (los feature branches conectarán sus pantallas a estas rutas).
    const val VEHICULOS = "vehiculos"
    const val KILOMETRAJE = "kilometraje"
    const val PLAN_MANTENIMIENTO = "plan_mantenimiento"
    const val MANTENIMIENTOS = "mantenimientos"
    const val ALERTAS = "alertas"
    const val HISTORIAL = "historial"
}
