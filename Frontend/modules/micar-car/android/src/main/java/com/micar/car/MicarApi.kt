package com.micar.car

import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class Veiculo(
  val id: String,
  val apelido: String,
  val placa: String,
  val odometroAtual: Long?,
)

class UnauthorizedException : Exception()

class NotFoundException : Exception()

class ApiException(message: String) : Exception(message)

class MicarApi(private val apiUrl: String, private val token: String) {
  fun getVeiculos(): List<Veiculo> {
    val veiculos = JSONArray(request("GET", "/Veiculos"))
    return (0 until veiculos.length()).map { parseVeiculo(veiculos.getJSONObject(it)) }
  }

  fun getVeiculo(id: String): Veiculo = parseVeiculo(JSONObject(request("GET", "/Veiculos/$id")))

  fun createRegistroOdometro(veiculoId: String, odometro: Long) {
    val body = JSONObject()
      .put("veiculoId", veiculoId)
      .put("odometro", odometro)
    request("POST", "/RegistrosOdometro", body.toString())
  }

  private fun request(method: String, path: String, body: String? = null): String {
    val connection = URL(apiUrl.trimEnd('/') + path).openConnection() as HttpURLConnection

    try {
      connection.requestMethod = method
      connection.connectTimeout = TIMEOUT_MS
      connection.readTimeout = TIMEOUT_MS
      connection.setRequestProperty("Authorization", "Bearer $token")
      connection.setRequestProperty("Accept", "application/json")

      if (body != null) {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        connection.outputStream.use { it.write(body.toByteArray()) }
      }

      when (val status = connection.responseCode) {
        HttpURLConnection.HTTP_UNAUTHORIZED -> throw UnauthorizedException()
        HttpURLConnection.HTTP_NOT_FOUND -> throw NotFoundException()
        !in 200..299 -> throw ApiException(readErrorMessage(connection, status))
      }

      return connection.inputStream.bufferedReader().use { it.readText() }
    } finally {
      connection.disconnect()
    }
  }

  private fun readErrorMessage(connection: HttpURLConnection, status: Int): String {
    val text = connection.errorStream?.bufferedReader()?.use { it.readText() }.orEmpty()
    val problem = runCatching { JSONObject(text) }.getOrNull()
    val message = problem?.optString("detail")?.ifBlank { problem.optString("title") }
    return message?.ifBlank { null } ?: "$FALLBACK_ERROR_MESSAGE ($status)"
  }

  private fun parseVeiculo(json: JSONObject) = Veiculo(
    id = json.getString("id"),
    apelido = json.getString("apelido"),
    placa = json.getString("placa"),
    odometroAtual = if (json.isNull("odometroAtual")) null else json.getDouble("odometroAtual").toLong(),
  )

  private companion object {
    const val TIMEOUT_MS = 15_000
    const val FALLBACK_ERROR_MESSAGE = "Não foi possível processar a sua requisição."
  }
}
