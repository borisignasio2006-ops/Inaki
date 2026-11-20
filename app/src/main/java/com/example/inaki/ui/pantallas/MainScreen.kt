package com.example.inaki.ui.pantallas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.ModoApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(estado: EstadoApp) {
    var tabSeleccionadaIndex by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estado.snackbarMensaje) {
        estado.snackbarMensaje?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            estado.limpiarMensaje()
        }
    }

    BackHandler {
        // Prevent accidental back exit or handle custom back
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar {
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
                    icon = { Text("👤", fontSize = 18.sp) },
                    label = { Text("Perfil") }
                )
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
                        2 -> PerfilScreen(estado)
                    }
                }
                ModoApp.ANUNCIANTE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Modo Anunciante",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Disponible en el Commit 4")
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { estado.cambiarModo(ModoApp.BUSCADOR) }) {
                            Text("Volver a Buscador")
                        }
                    }
                }
                ModoApp.ADMINISTRADOR -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Modo Administrador",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Disponible en el Commit 4")
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { estado.cambiarModo(ModoApp.BUSCADOR) }) {
                            Text("Volver a Buscador")
                        }
                    }
                }
            }
        }
    }
}
