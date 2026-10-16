package com.micar.app.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ErroDialog(mensagem: String?, onFechar: () -> Unit) {
    if (mensagem == null) return

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Ocorreu um erro com a sua requisição") },
        text = { Text(mensagem) },
        confirmButton = { TextButton(onClick = onFechar) { Text("OK") } },
    )
}
