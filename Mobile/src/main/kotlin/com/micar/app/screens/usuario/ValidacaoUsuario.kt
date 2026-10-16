package com.micar.app.screens.usuario

object ValidacaoUsuario {
    const val SENHA_TAMANHO_MINIMO = 6

    private val formatoEmail = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    fun validarNome(nome: String): String? =
        if (nome.isBlank()) "Informe o nome." else null

    fun validarEmail(email: String): String? =
        if (formatoEmail.matches(email.trim())) null else "Informe um email válido."

    fun validarSenhaLogin(senha: String): String? =
        if (senha.isEmpty()) "Informe a senha." else null

    fun validarSenhaCadastro(senha: String): String? =
        if (senha.length < SENHA_TAMANHO_MINIMO) {
            "A senha deve ter no mínimo $SENHA_TAMANHO_MINIMO caracteres."
        } else {
            null
        }
}
