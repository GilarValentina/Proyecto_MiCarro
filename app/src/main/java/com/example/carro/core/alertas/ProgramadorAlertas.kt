package com.example.carro.core.alertas

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Programa la revisión periódica de vencimientos en segundo plano (RF-28).
 * Usa WorkManager con una periodicidad diaria; sobrevive a reinicios del dispositivo.
 */
object ProgramadorAlertas {

    private const val TRABAJO_PERIODICO = "revision_vencimientos"

    fun programar(context: Context) {
        val solicitud = PeriodicWorkRequestBuilder<AlertasWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            TRABAJO_PERIODICO,
            ExistingPeriodicWorkPolicy.KEEP,
            solicitud
        )
    }
}
