package com.micar.car

import android.content.Intent
import android.content.pm.ApplicationInfo
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class MicarCarAppService : CarAppService() {
  override fun createHostValidator(): HostValidator {
    val isDebuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0

    if (isDebuggable) {
      return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    return HostValidator.Builder(applicationContext)
      .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
      .build()
  }

  override fun onCreateSession(): Session = MicarCarSession()
}

class MicarCarSession : Session() {
  override fun onCreateScreen(intent: Intent): Screen = RegistroOdometroScreen(carContext)
}
