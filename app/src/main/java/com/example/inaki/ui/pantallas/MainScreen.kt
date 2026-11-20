package com.example.inaki.ui.pantallas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.ModoApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(estado: EstadoApp) {
    var tabSeleccionadaIndex by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Reset tab index when mode changes
    LaunchedEffect(estado.modoActivo) {
        tabSeleccionadaIndex = 0
    }

    LaunchedEffect(estado.snackbarMensaje) {
        estado.snackbarMensaje?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            estado.limpiarMensaje()
        }
    }

    BackHandler {
        // Prevent accidental back exit
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
                when (estado.modoActivo) {
                    ModoApp.BUSCADOR -> {
                        NavigationBarItem(
                            selected = tabSeleccionadaIndex == 0,
                            onClick = { tabSeleccionadaIndex = 0 },
                            icon = { Text("🏠", fontSize = 18.sp) },
                            label = { Text("Explorar") }
                        )
                        NavigationBarItem(
                            selected = tabSeleccionadaIndex == 1,
                            onClick = { tabSeleccionadaIndex = 1 },
                            icon = { Text("❤️", fontSize = 18.sp) },
                            label = { Text("Favoritos") }
                        )
                        NavigationBarItem(
                            selected = tabSeleccionadaIndex == 2,
                            onClick = { tabSeleccionadaIndex = 2 },
                            icon = { Text("🔔", fontSize = 18.sp) },
                            label = { Text("Alertas") }
                        )
                        NavigationBarItem(
                            selected = tabSeleccionadaIndex == 3,
                            onClick = { tabSeleccionadaIndex = 3 },
                            icon = { Text("💬", fontSize = 18.sp) },
                            label = { Text("Consultas") }
                        )
                        NavigationBarItem(
                            selected = tabSeleccionadaIndex == 4,
                            onClick = { tabSeleccionadaIndex = 4 },
                            icon = { Text("👤", fontSize = 18.sp) },
                            label = { Text("Perfil") }
                        )
                    }
                    ModoApp.ANUNCIANTE -> {
                        NavigationBarItem(
                            selected = tabSeleccionadaIndex == 0,
                            onClick = { tabSeleccionadaIndex = 0 },
                            icon = { Text("💬", fontSize = 18.sp) },
                            label = { Text("Consultas") }
                        )
                        NavigationBarItem(
                            selected = tabSeleccionadaIndex == 1,
                            onClick = { tabSeleccionadaIndex = 1 },
                            icon = { Text("👤", fontSize = 18.sp) },
                            label = { Text("Perfil") }
                        )
                    }
                    ModoApp.ADMINISTRADOR -> {
                        NavigationBarItem(
                            selected = tabSeleccionadaIndex == 0,
                            onClick = { tabSeleccionadaIndex = 0 },
                            icon = { Text("👤", fontSize = 18.sp) },
                            label = { Text("Perfil") }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (estado.modoActivo) {
                ModoApp.BUSCADOR -> {
                    when (tabSeleccionadaIndex) {
                        0 -> ExplorarScreen(estado)
                        1 -> FavoritosScreen(estado)
                        2 -> AlertasScreen(estado)
                        3 -> ConsultasScreen(estado)
                        4 -> PerfilScreen(estado)
                    }
                }
                ModoApp.ANUNCIANTE -> {
                    when (tabSeleccionadaIndex) {
                        0 -> ConsultasScreen(estado)
                        1 -> PerfilScreen(estado)
                    }
                }
                ModoApp.ADMINISTRADOR -> {
                    PerfilScreen(estado)
                }
            }
        }
    }
}
