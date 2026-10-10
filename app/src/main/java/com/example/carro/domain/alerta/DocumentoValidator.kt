package com.example.carro.domain.alerta

/**
 * Validación de un documento antes de guardarlo (RF-32, RNF-16).
 * No se permiten documentos sin fecha; el tipo siempre está definido por el enum.
 */
object DocumentoValidator {

    data class Resultado(val errores: List<String>) {
        val esValido: Boolean get() = errores.isEmpty()
    }

    fun validar(documento: Documento): Resultado {
        val errores = mutableListOf<String>()
        if (documento.tipo == TipoDocumento.OTRO && documento.nombre.isNullOrBlank()) {
            errores += "Indica un nombre para el documento cuando el tipo es \"Otro\"."
        }
        return Resultado(errores)
    }
}
