package com.example.carro

import android.app.Application
import com.example.carro.core.di.AppContainer

/** Punto de entrada de la aplicación; inicializa el contenedor de dependencias. */
class MiCarroApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
