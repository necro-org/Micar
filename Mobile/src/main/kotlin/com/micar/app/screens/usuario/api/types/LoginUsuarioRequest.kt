package com.micar.app.screens.usuario.api.types

import kotlinx.serialization.Serializable

@Serializable
data class LoginUsuarioRequest(
    val email: String,
    val senha: String,
)
