package com.micar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.micar.app.ui.theme.Cores

enum class VarianteBotao { Primario, Fantasma }

@Composable
fun MicarButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variante: VarianteBotao = VarianteBotao.Primario,
    carregando: Boolean = false,
    desabilitado: Boolean = false,
) {
    val inativo = desabilitado || carregando
    val primario = variante == VarianteBotao.Primario
    val formato = RoundedCornerShape(12.dp)

    val fundo = when {
        !primario -> Modifier
        inativo -> Modifier.background(Cores.Accent300, formato)
        else -> Modifier.background(Cores.Accent500, formato)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .clip(formato)
            .then(fundo)
            .clickable(enabled = !inativo, onClick = onClick)
            .padding(vertical = if (primario) 16.dp else 12.dp),
    ) {
        if (carregando) {
            CircularProgressIndicator(
                color = if (primario) Cores.Branco else Cores.Brand600,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp),
            )
        } else {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = if (primario) FontWeight.Bold else FontWeight.SemiBold,
                color = if (primario) Cores.Branco else Cores.Brand600,
            )
        }
    }
}
