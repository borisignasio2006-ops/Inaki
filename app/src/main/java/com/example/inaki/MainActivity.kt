package com.example.inaki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.navegacion.Pantalla
import com.example.inaki.ui.pantallas.*
import com.example.inaki.ui.theme.InakiTheme

class MainActivity : ComponentActivity() {
    private val estadoApp: EstadoApp by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InakiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (val pantalla = estadoApp.pantallaActual) {
                        is Pantalla.Login -> LoginScreen(estadoApp)
                        is Pantalla.Registro -> RegistroScreen(estadoApp)
                        is Pantalla.Main -> MainScreen(estadoApp)
                        is Pantalla.DetalleAnuncio -> DetalleAnuncioScreen(estadoApp, pantalla.anuncioId)
                    }
                }
            }
        }
    }
}
