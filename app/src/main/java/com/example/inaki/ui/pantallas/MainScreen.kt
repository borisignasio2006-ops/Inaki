package com.example.inaki.ui.pantallas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.ModoApp
import com.example.inaki.ui.theme.InakiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(estado: EstadoApp) {
    var tabSeleccionadaIndex by remember { mutableIntStateOf(0) }
    var mostrandoAsistenteAnuncio by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(estado.modoActivo) {
        tabSeleccionadaIndex = 0
        mostrandoAsistenteAnuncio = false
    }

    LaunchedEffect(estado.snackbarMensaje) {
        estado.snackbarMensaje?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            estado.limpiarMensaje()
        }
    }

    BackHandler {
        if (mostrandoAsistenteAnuncio) {
            mostrandoAsistenteAnuncio = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!mostrandoAsistenteAnuncio) {
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
                                icon = { Text("🏠", fontSize = 18.sp) },
                                label = { Text("Mis Anuncios") }
                            )
                            NavigationBarItem(
                                selected = tabSeleccionadaIndex == 1,
                                onClick = { tabSeleccionadaIndex = 1 },
                                icon = { Text("💬", fontSize = 18.sp) },
                                label = { Text("Consultas") }
                            )
                            NavigationBarItem(
                                selected = tabSeleccionadaIndex == 2,
                                onClick = { tabSeleccionadaIndex = 2 },
                                icon = { Text("🛡️", fontSize = 18.sp) },
                                label = { Text("Verificación") }
                            )
                            NavigationBarItem(
                                selected = tabSeleccionadaIndex == 3,
                                onClick = { tabSeleccionadaIndex = 3 },
                                icon = { Text("⭐", fontSize = 18.sp) },
                                label = { Text("Planes") }
                            )
                            NavigationBarItem(
                                selected = tabSeleccionadaIndex == 4,
                                onClick = { tabSeleccionadaIndex = 4 },
                                icon = { Text("👤", fontSize = 18.sp) },
                                label = { Text("Perfil") }
                            )
                        }
                        ModoApp.ADMINISTRADOR -> {
                            NavigationBarItem(
                                selected = tabSeleccionadaIndex == 0,
                                onClick = { tabSeleccionadaIndex = 0 },
                                icon = { Text("🛡️", fontSize = 18.sp) },
                                label = { Text("Moderación") }
                            )
                            NavigationBarItem(
                                selected = tabSeleccionadaIndex == 1,
                                onClick = { tabSeleccionadaIndex = 1 },
                                icon = { Text("✔️", fontSize = 18.sp) },
                                label = { Text("Verificaciones") }
                            )
                            NavigationBarItem(
                                selected = tabSeleccionadaIndex == 2,
                                onClick = { tabSeleccionadaIndex = 2 },
                                icon = { Text("⚠️", fontSize = 18.sp) },
                                label = { Text("Reportes") }
                            )
                            NavigationBarItem(
                                selected = tabSeleccionadaIndex == 3,
                                onClick = { tabSeleccionadaIndex = 3 },
                                icon = { Text("👤", fontSize = 18.sp) },
                                label = { Text("Perfil") }
                            )
                        }
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
            if (mostrandoAsistenteAnuncio) {
                AsistenteAnuncioScreen(estado) {
                    mostrandoAsistenteAnuncio = false
                }
            } else {
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
                            0 -> MisAnunciosScreen(
                                estado = estado,
                                onIrANuevoAnuncio = { mostrandoAsistenteAnuncio = true },
                                onIrAPlanes = { tabSeleccionadaIndex = 3 }
                            )
                            1 -> ConsultasScreen(estado)
                            2 -> VerificacionScreen(estado)
                            3 -> PlanesScreen(estado)
                            4 -> PerfilScreen(estado)
                        }
                    }
                    ModoApp.ADMINISTRADOR -> {
                        when (tabSeleccionadaIndex) {
                            0 -> AdminModeracionScreen(estado)
                            1 -> AdminVerificacionesScreen(estado)
                            2 -> AdminReportesScreen(estado)
                            3 -> PerfilScreen(estado)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    InakiTheme {
        val estado = remember { EstadoApp().apply { usuarioActual = usuarios.firstOrNull() } }
        MainScreen(estado = estado)
    }
}
