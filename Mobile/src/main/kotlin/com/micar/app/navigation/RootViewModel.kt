package com.micar.app.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.micar.app.auth.Sessao
import com.micar.app.auth.SessaoStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RootViewModel(private val sessaoStore: SessaoStore) : ViewModel() {
    val sessao: StateFlow<Sessao> = sessaoStore.sessao

    init {
        viewModelScope.launch { sessaoStore.carregar() }
    }
}
