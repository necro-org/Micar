package com.micar.app.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.micar.app.ui.components.MicarButton
import com.micar.app.ui.components.VarianteBotao
import com.micar.app.ui.theme.Cores
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Cores.Brand50)
            .safeDrawingPadding()
            .padding(24.dp),
    ) {
        Text(
            text = "Olá, ${viewModel.nomeUsuario ?: "motorista"}!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Cores.Brand900,
            modifier = Modifier.padding(bottom = 24.dp),
        )

        MicarButton(label = "Sair", onClick = viewModel::sair, variante = VarianteBotao.Fantasma)
    }
}
