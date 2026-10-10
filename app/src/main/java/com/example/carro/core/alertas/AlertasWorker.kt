package com.example.carro.core.alertas

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.carro.MiCarroApplication

/**
 * Trabajo en segundo plano que revisa los vencimientos y publica alertas locales (RF-28, RF-33).
 * Se ejecuta de forma periódica mediante WorkManager, sin requerir conexión (RNF offline-first).
 */
class AlertasWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = (applicationContext as MiCarroApplication).container.alertasRepository
        if (!repository.configuracionActual().alertasActivas) return Result.success()
        return try {
            val pendientes = repository.sincronizarAlertas()
            NotificacionesAlertas.notificar(applicationContext, pendientes)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
