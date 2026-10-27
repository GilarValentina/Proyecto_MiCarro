package com.example.carro.domain.vehiculo

/** Campos del formulario de vehículo que pueden tener error. */
enum class CampoVehiculo { PLACA, MARCA, LINEA, ANIO, KILOMETRAJE, VIN }

/** Resultado de validar un vehículo: un mensaje por campo con error. */
data class ResultadoValidacionVehiculo(
    val errores: Map<CampoVehiculo, String> = emptyMap()
) {
    val esValido: Boolean get() = errores.isEmpty()
}

/**
 * Validaciones del registro de vehículos (RF-01, RF-02, RF-05, RNF-16).
 * Funciones puras para facilitar las pruebas unitarias (RNF-13).
 */
object VehiculoValidator {

    const val ANIO_MINIMO = 1950
    private val PLACA = Regex("^[A-Z0-9]{5,7}$")
    private val VIN = Regex("^[A-HJ-NPR-Z0-9]{17}$")

    /**
     * Normaliza la placa para compararla y guardarla siempre igual:
     * mayúsculas, sin espacios ni guiones ("abc-123" → "ABC123").
     */
    fun normalizarPlaca(placa: String): String =
        placa.uppercase().filter { it.isLetterOrDigit() }

    /**
     * @param placaDuplicada si ya existe otro vehículo con la misma placa normalizada (RF-05).
     * @param anioActual año de referencia; se admite hasta el año siguiente (modelos nuevos).
     */
    fun validar(
        vehiculo: Vehiculo,
        placaDuplicada: Boolean,
        anioActual: Int
    ): ResultadoValidacionVehiculo {
        val errores = mutableMapOf<CampoVehiculo, String>()
        val placa = normalizarPlaca(vehiculo.placa)

        when {
            placa.isEmpty() -> errores[CampoVehiculo.PLACA] = "La placa es obligatoria."
            !PLACA.matches(placa) -> errores[CampoVehiculo.PLACA] = "La placa debe tener entre 5 y 7 letras o números."
            placaDuplicada -> errores[CampoVehiculo.PLACA] = "Ya existe un vehículo con la placa $placa."
        }
        if (vehiculo.marca.isBlank()) errores[CampoVehiculo.MARCA] = "La marca es obligatoria."
        if (vehiculo.linea.isBlank()) errores[CampoVehiculo.LINEA] = "La línea es obligatoria."
        if (vehiculo.anio !in ANIO_MINIMO..anioActual + 1) {
            errores[CampoVehiculo.ANIO] = "El modelo debe estar entre $ANIO_MINIMO y ${anioActual + 1}."
        }
        if (vehiculo.kilometrajeActual < 0) {
            errores[CampoVehiculo.KILOMETRAJE] = "Ingresa el kilometraje actual (0 o más)."
        }
        vehiculo.vin?.takeIf { it.isNotBlank() }?.let { vin ->
            if (!VIN.matches(vin.uppercase())) {
                errores[CampoVehiculo.VIN] = "El VIN debe tener 17 caracteres (sin I, O ni Q)."
            }
        }
        return ResultadoValidacionVehiculo(errores)
    }
}
