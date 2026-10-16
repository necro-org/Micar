package com.micar.app.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micar.app.auth.Sessao
import com.micar.app.auth.SessaoStore
import com.micar.app.screens.usuario.AuthRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sessaoStore: SessaoStore,
    private val authRepository: AuthRepository,
) : ViewModel() {
    val nomeUsuario: String?
        get() = (sessaoStore.sessao.value as? Sessao.Autenticada)?.usuario?.nome

    fun sair() {
        viewModelScope.launch { authRepository.sair() }
    }
}
