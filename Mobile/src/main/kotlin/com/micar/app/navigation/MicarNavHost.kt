package com.micar.app.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.micar.app.screens.home.HomeScreen
import com.micar.app.ui.theme.Cores
import com.micar.app.screens.usuario.cadastro.CadastroScreen
import com.micar.app.screens.usuario.login.LoginScreen
import com.micar.app.auth.Sessao
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
private data object LoginRoute

@Serializable
private data object CadastroRoute

@Composable
fun MicarNavHost(viewModel: RootViewModel = koinViewModel()) {
    val sessao by viewModel.sessao.collectAsStateWithLifecycle()

    when (sessao) {
        Sessao.Carregando -> Carregando()
        Sessao.Anonima -> AuthNavHost()
        is Sessao.Autenticada -> HomeScreen()
    }
}

@Composable
private fun AuthNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = LoginRoute) {
        composable<LoginRoute> {
            LoginScreen(onIrParaCadastro = { navController.navigate(CadastroRoute) })
        }
        composable<CadastroRoute> {
            CadastroScreen(
                onIrParaLogin = {
                    if (!navController.popBackStack()) navController.navigate(LoginRoute)
                },
            )
        }
    }
}

@Composable
private fun Carregando() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(Cores.Brand50),
    ) {
        CircularProgressIndicator(color = Cores.Brand500)
    }
}
