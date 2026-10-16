package com.micar.app.screens.usuario.login

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.micar.app.ui.components.AuthFormScreen
import com.micar.app.ui.components.ErroDialog
import com.micar.app.ui.components.MicarButton
import com.micar.app.ui.components.MicarTextField
import com.micar.app.ui.components.VarianteBotao
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onIrParaCadastro: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    AuthFormScreen(
        titulo = "Entrar",
        rodape = {
            MicarButton(
                label = "Não tem uma conta? Cadastre-se",
                onClick = onIrParaCadastro,
                variante = VarianteBotao.Fantasma,
            )
        },
    ) {
        MicarTextField(
            label = "Email",
            valor = estado.email,
            onValorChange = viewModel::alterarEmail,
            placeholder = "email@email.com",
            erro = estado.erroEmail,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next,
            ),
        )

        MicarTextField(
            label = "Senha",
            valor = estado.senha,
            onValorChange = viewModel::alterarSenha,
            placeholder = "Sua senha",
            erro = estado.erroSenha,
            senha = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.entrar() }),
        )

        MicarButton(
            label = "Entrar",
            onClick = viewModel::entrar,
            carregando = estado.carregando,
        )
    }

    ErroDialog(mensagem = estado.erroGeral, onFechar = viewModel::fecharErro)
}
