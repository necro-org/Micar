package com.micar.app.screens.usuario

import com.micar.app.auth.SessaoStore
import com.micar.app.screens.usuario.api.services.CreateUsuarioService
import com.micar.app.screens.usuario.api.services.LoginUsuarioService
import com.micar.app.screens.usuario.api.types.CreateUsuarioRequest
import com.micar.app.screens.usuario.api.types.LoginUsuarioRequest

class AuthRepository(
    private val loginUsuario: LoginUsuarioService,
    private val createUsuario: CreateUsuarioService,
    private val sessaoStore: SessaoStore,
) {
    suspend fun login(email: String, senha: String) {
        val response = loginUsuario(LoginUsuarioRequest(email.trim(), senha))
        sessaoStore.iniciar(response.token)
    }

    suspend fun criarConta(nome: String, email: String, senha: String) {
        val response = createUsuario(CreateUsuarioRequest(nome.trim(), email.trim(), senha))
        sessaoStore.iniciar(response.token)
    }

    suspend fun sair() {
        sessaoStore.encerrar()
    }
}
