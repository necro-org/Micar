package com.micar.app.screens.usuario.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micar.app.api.ApiException
import com.micar.app.screens.usuario.AuthRepository
import com.micar.app.screens.usuario.ValidacaoUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val erroEmail: String? = null,
    val erroSenha: String? = null,
    val carregando: Boolean = false,
    val erroGeral: String? = null,
    val tentouEnviar: Boolean = false,
) {
    val temErros get() = erroEmail != null || erroSenha != null

    fun validado() = copy(
        erroEmail = ValidacaoUsuario.validarEmail(email),
        erroSenha = ValidacaoUsuario.validarSenhaLogin(senha),
    )

    fun revalidadoSeEnviado() = if (tentouEnviar) validado() else this
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun alterarEmail(email: String) = _estado.update { it.copy(email = email).revalidadoSeEnviado() }

    fun alterarSenha(senha: String) = _estado.update { it.copy(senha = senha).revalidadoSeEnviado() }

    fun fecharErro() = _estado.update { it.copy(erroGeral = null) }

    fun entrar() {
        val atual = _estado.value
        if (atual.carregando) return

        val validado = atual.validado().copy(tentouEnviar = true)
        _estado.value = validado
        if (validado.temErros) return

        viewModelScope.launch {
            _estado.update { it.copy(carregando = true) }
            try {
                authRepository.login(validado.email, validado.senha)
            } catch (e: ApiException) {
                _estado.update { it.copy(erroGeral = e.message) }
            } finally {
                _estado.update { it.copy(carregando = false) }
            }
        }
    }
}
