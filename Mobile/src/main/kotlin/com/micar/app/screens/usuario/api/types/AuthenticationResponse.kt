package com.micar.app.screens.usuario.api.types

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationResponse(
    val token: String,
)
