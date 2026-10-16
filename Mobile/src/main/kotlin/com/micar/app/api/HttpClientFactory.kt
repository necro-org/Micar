package com.micar.app.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.bearerAuth
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun criarHttpClient(
    baseUrl: String,
    tokenAtual: () -> String?,
): HttpClient = HttpClient(OkHttp) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }

    install(
        createClientPlugin("Autenticacao") {
            onRequest { request, _ ->
                tokenAtual()?.let { request.bearerAuth(it) }
            }
        },
    )

    defaultRequest {
        url(baseUrl)
        contentType(ContentType.Application.Json)
    }
}
