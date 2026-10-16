package com.micar.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.micar.app.navigation.MicarNavHost
import com.micar.app.ui.theme.MicarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MicarTheme {
                MicarNavHost()
            }
        }
    }
}
