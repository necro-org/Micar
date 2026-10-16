package com.micar.app.screens.usuario.api.types

import kotlinx.serialization.Serializable

@Serializable
data class CreateUsuarioRequest(
    val nome: String,
    val email: String,
    val senha: String,
)
