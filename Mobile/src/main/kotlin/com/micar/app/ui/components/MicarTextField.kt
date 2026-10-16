package com.micar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.micar.app.ui.theme.Cores

@Composable
fun MicarTextField(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    erro: String? = null,
    senha: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var oculto by rememberSaveable { mutableStateOf(senha) }
    val formato = RoundedCornerShape(12.dp)
    val estiloTexto = TextStyle(fontSize = 16.sp, color = Cores.Brand900)

    Column(modifier = modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Cores.Brand900,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Cores.Brand200, formato)
                .background(Cores.Branco, formato)
                .padding(horizontal = 16.dp),
        ) {
            BasicTextField(
                value = valor,
                onValueChange = onValorChange,
                singleLine = true,
                textStyle = estiloTexto,
                cursorBrush = SolidColor(Cores.Brand600),
                visualTransformation = if (senha && oculto) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = if (senha) keyboardOptions.copy(keyboardType = KeyboardType.Password) else keyboardOptions,
                keyboardActions = keyboardActions,
                modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                decorationBox = { campo ->
                    Box {
                        if (valor.isEmpty() && placeholder != null) {
                            Text(text = placeholder, style = estiloTexto.copy(color = Cores.Placeholder))
                        }
                        campo()
                    }
                },
            )

            if (senha) {
                Text(
                    text = if (oculto) "Mostrar" else "Ocultar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Cores.Brand500,
                    modifier = Modifier
                        .clickable { oculto = !oculto }
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
                )
            }
        }

        if (erro != null) {
            Text(
                text = erro,
                fontSize = 14.sp,
                color = Cores.Danger600,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
