package com.micar.app.di

import com.micar.app.BuildConfig
import com.micar.app.api.criarHttpClient
import com.micar.app.auth.SessaoStore
import com.micar.app.auth.TokenStorage
import com.micar.app.screens.home.HomeViewModel
import com.micar.app.navigation.RootViewModel
import com.micar.app.screens.usuario.AuthRepository
import com.micar.app.screens.usuario.api.services.CreateUsuarioService
import com.micar.app.screens.usuario.api.services.LoginUsuarioService
import com.micar.app.screens.usuario.cadastro.CadastroViewModel
import com.micar.app.screens.usuario.login.LoginViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::TokenStorage)
    singleOf(::SessaoStore)
    single { criarHttpClient(BuildConfig.API_URL) { get<SessaoStore>().tokenAtual() } }

    singleOf(::LoginUsuarioService)
    singleOf(::CreateUsuarioService)
    singleOf(::AuthRepository)

    viewModelOf(::RootViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::CadastroViewModel)
    viewModelOf(::HomeViewModel)
}
