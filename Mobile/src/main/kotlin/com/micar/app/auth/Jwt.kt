package com.micar.app.auth

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

data class JwtPayload(
    val nome: String?,
    val email: String?,
    val expiraEmSegundos: Long?,
)

object Jwt {
    @OptIn(ExperimentalEncodingApi::class)
    private val base64Url = Base64.UrlSafe.withPadding(Base64.PaddingOption.ABSENT_OPTIONAL)

    @OptIn(ExperimentalEncodingApi::class)
    fun decodificar(token: String): JwtPayload? = runCatching {
        val payload = token.split(".")[1]
        val json = Json.parseToJsonElement(base64Url.decode(payload).decodeToString()).jsonObject
        JwtPayload(
            nome = json.texto("name"),
            email = json.texto("email"),
            expiraEmSegundos = json["exp"]?.jsonPrimitive?.longOrNull,
        )
    }.getOrNull()

    fun estaExpirado(token: String, agoraEmSegundos: Long = System.currentTimeMillis() / 1000): Boolean {
        val expiraEm = decodificar(token)?.expiraEmSegundos ?: return false
        return agoraEmSegundos >= expiraEm
    }

    private fun JsonObject.texto(chave: String): String? = get(chave)?.jsonPrimitive?.contentOrNull
}
