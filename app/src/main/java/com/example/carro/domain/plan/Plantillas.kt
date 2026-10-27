package com.example.carro.domain.plan

/**
 * Categorías iniciales del plan de mantenimiento (RF-12).
 * Son editables por el usuario; estas solo siembran la base la primera vez.
 */
object CategoriasPredeterminadas {
    val nombres = listOf(
        "Aceite",
        "Filtros",
        "Frenos",
        "Llantas",
        "Batería",
        "Refrigeración",
        "Suspensión",
        "Transmisión",
        "Otros"
    )
}

/**
 * Plantilla de mantenimiento común para agilizar la creación del plan (RF-16).
 * Los intervalos son sugerencias editables al crear la actividad.
 */
data class PlantillaMantenimiento(
    val nombre: String,
    val categoria: String,
    val intervaloDias: Int? = null,
    val intervaloKm: Long? = null
)

object PlantillasPredeterminadas {
    val lista = listOf(
        PlantillaMantenimiento("Cambio de aceite", "Aceite", intervaloDias = 180, intervaloKm = 5_000),
        PlantillaMantenimiento("Cambio de filtro de aceite", "Filtros", intervaloDias = 180, intervaloKm = 5_000),
        PlantillaMantenimiento("Cambio de filtro de aire", "Filtros", intervaloDias = 365, intervaloKm = 15_000),
        PlantillaMantenimiento("Revisión de frenos", "Frenos", intervaloDias = 365, intervaloKm = 10_000),
        PlantillaMantenimiento("Rotación de llantas", "Llantas", intervaloKm = 10_000),
        PlantillaMantenimiento("Revisión de batería", "Batería", intervaloDias = 365),
        PlantillaMantenimiento("Cambio de refrigerante", "Refrigeración", intervaloDias = 730, intervaloKm = 40_000),
        PlantillaMantenimiento("Revisión de suspensión", "Suspensión", intervaloKm = 20_000),
        PlantillaMantenimiento("Cambio de aceite de transmisión", "Transmisión", intervaloKm = 40_000)
    )
}
