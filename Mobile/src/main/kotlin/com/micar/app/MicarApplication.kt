package com.micar.app

import android.app.Application
import com.micar.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MicarApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MicarApplication)
            modules(appModule)
        }
    }
}
