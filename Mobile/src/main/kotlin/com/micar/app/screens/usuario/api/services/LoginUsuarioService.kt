package com.micar.app.screens.usuario.api.services

import com.micar.app.api.requisitar
import com.micar.app.screens.usuario.api.types.AuthenticationResponse
import com.micar.app.screens.usuario.api.types.LoginUsuarioRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class LoginUsuarioService(private val httpClient: HttpClient) {
    suspend operator fun invoke(request: LoginUsuarioRequest): AuthenticationResponse = requisitar {
        httpClient.post("Usuarios/login") { setBody(request) }.body()
    }
}
