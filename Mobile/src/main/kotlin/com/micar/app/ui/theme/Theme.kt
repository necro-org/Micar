package com.micar.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val esquemaDeCores = lightColorScheme(
    primary = Cores.Brand600,
    onPrimary = Cores.Branco,
    secondary = Cores.Accent500,
    onSecondary = Cores.Branco,
    background = Cores.Brand50,
    onBackground = Cores.Brand900,
    surface = Cores.Branco,
    onSurface = Cores.Brand900,
    error = Cores.Danger600,
)

@Composable
fun MicarTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = esquemaDeCores, content = content)
}
