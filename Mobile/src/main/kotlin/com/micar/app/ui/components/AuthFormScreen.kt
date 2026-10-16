package com.micar.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.micar.app.ui.theme.Cores

@Composable
fun AuthFormScreen(
    titulo: String,
    rodape: @Composable () -> Unit,
    conteudo: @Composable ColumnScope.() -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Cores.Brand50)
            .safeDrawingPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
        ) {
            Text(
                text = "Micar",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Cores.Brand900,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 40.dp),
            )

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Cores.Branco,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = titulo,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Cores.Brand900,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    conteudo()
                }
            }

            rodape()
        }
    }
}
