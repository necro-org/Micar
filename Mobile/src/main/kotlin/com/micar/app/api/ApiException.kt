package com.micar.app.api

import io.ktor.client.call.body
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.Serializable

const val MENSAGEM_ERRO_PADRAO = "Não foi possível processar a sua requisição."

class ApiException(message: String) : Exception(message)

@Serializable
internal data class ProblemDetails(
    val title: String? = null,
    val detail: String? = null,
)

internal suspend fun <T> requisitar(bloco: suspend () -> T): T =
    try {
        bloco()
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        val problema = runCatching { e.response.body<ProblemDetails>() }.getOrNull()
        throw ApiException(problema?.detail ?: problema?.title ?: MENSAGEM_ERRO_PADRAO)
    } catch (e: Exception) {
        throw ApiException(MENSAGEM_ERRO_PADRAO)
    }
