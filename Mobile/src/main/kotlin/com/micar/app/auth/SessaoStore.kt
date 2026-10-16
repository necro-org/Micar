package com.micar.app.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface Sessao {
    data object Carregando : Sessao
    data object Anonima : Sessao
    data class Autenticada(val token: String, val usuario: JwtPayload?) : Sessao
}

class SessaoStore(private val tokenStorage: TokenStorage) {
    private val _sessao = MutableStateFlow<Sessao>(Sessao.Carregando)
    val sessao: StateFlow<Sessao> = _sessao.asStateFlow()

    suspend fun carregar() {
        val tokenSalvo = tokenStorage.ler()
        val tokenValido = tokenSalvo?.takeUnless(Jwt::estaExpirado)

        if (tokenSalvo != null && tokenValido == null) {
            tokenStorage.remover()
        }

        _sessao.value = tokenValido?.let(::autenticada) ?: Sessao.Anonima
    }

    suspend fun iniciar(token: String) {
        tokenStorage.salvar(token)
        _sessao.value = autenticada(token)
    }

    suspend fun encerrar() {
        tokenStorage.remover()
        _sessao.value = Sessao.Anonima
    }

    fun tokenAtual(): String? = (sessao.value as? Sessao.Autenticada)?.token

    private fun autenticada(token: String) = Sessao.Autenticada(token, Jwt.decodificar(token))
}
