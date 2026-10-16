package com.micar.car

import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition

class MicarCarModule : Module() {
  override fun definition() = ModuleDefinition {
    Name("MicarCar")

    Function("setSession") { token: String, apiUrl: String ->
      sessionStore().saveSession(token, apiUrl)
    }

    Function("clearSession") {
      sessionStore().clear()
    }
  }

  private fun sessionStore() = CarSessionStore(requireNotNull(appContext.reactContext))
}
