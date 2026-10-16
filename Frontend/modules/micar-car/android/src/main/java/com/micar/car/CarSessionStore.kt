package com.micar.car

import android.content.Context

class CarSessionStore(context: Context) {
  private val preferences =
    context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

  val token: String?
    get() = preferences.getString(KEY_TOKEN, null)

  val apiUrl: String?
    get() = preferences.getString(KEY_API_URL, null)

  var veiculoId: String?
    get() = preferences.getString(KEY_VEICULO_ID, null)
    set(value) {
      preferences.edit().putString(KEY_VEICULO_ID, value).apply()
    }

  fun saveSession(token: String, apiUrl: String) {
    preferences.edit()
      .putString(KEY_TOKEN, token)
      .putString(KEY_API_URL, apiUrl)
      .apply()
  }

  fun clear() {
    preferences.edit().clear().apply()
  }

  private companion object {
    const val PREFERENCES_NAME = "micar_car_session"
    const val KEY_TOKEN = "token"
    const val KEY_API_URL = "apiUrl"
    const val KEY_VEICULO_ID = "veiculoId"
  }
}
