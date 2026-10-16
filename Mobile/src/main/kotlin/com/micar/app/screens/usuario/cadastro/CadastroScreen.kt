package com.micar.app.screens.usuario.cadastro

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
import com.micar.app.screens.usuario.ValidacaoUsuario
import org.koin.androidx.compose.koinViewModel

@Composable
fun CadastroScreen(
    onIrParaLogin: () -> Unit,
    viewModel: CadastroViewModel = koinViewModel(),
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    AuthFormScreen(
        titulo = "Criar conta",
        rodape = {
            MicarButton(
                label = "Já tem uma conta? Entrar",
                onClick = onIrParaLogin,
                variante = VarianteBotao.Fantasma,
            )
        },
    ) {
        MicarTextField(
            label = "Nome",
            valor = estado.nome,
            onValorChange = viewModel::alterarNome,
            placeholder = "Seu nome",
            erro = estado.erroNome,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next,
            ),
        )

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
            placeholder = "Mínimo ${ValidacaoUsuario.SENHA_TAMANHO_MINIMO} caracteres",
            erro = estado.erroSenha,
            senha = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { viewModel.criarConta() }),
        )

        MicarButton(
            label = "Criar conta",
            onClick = viewModel::criarConta,
            carregando = estado.carregando,
        )
    }

    ErroDialog(mensagem = estado.erroGeral, onFechar = viewModel::fecharErro)
}
