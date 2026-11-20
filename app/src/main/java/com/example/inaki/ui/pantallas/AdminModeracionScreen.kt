package com.example.inaki.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.dominio.modelo.*
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.theme.InakiTheme

@Composable
fun AdminModeracionScreen(estado: EstadoApp) {
    estado.revision
    val anuncios = estado.anuncios

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Moderación de Anuncios",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (anuncios.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay anuncios en el sistema", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(anuncios) { anuncio ->
                    val estadoStr = when (anuncio.estado) {
                        is Borrador -> "Borrador"
                        is Publicado -> "Publicado"
                        is Pausado -> "Pausado"
                        is Cerrado -> "Cerrado"
                        else -> "Desconocido"
                    }
                    val duenho = estado.usuarios.find { u -> u.anuncios.any { it.id == anuncio.id } }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = anuncio.titulo,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1
                                )
                                AssistChip(
                                    onClick = {},
                                    label = { Text(estadoStr, fontSize = 10.sp) }
                                )
                            }
                            Text("Propietario: ${duenho?.nombre ?: "Desconocido"}", style = MaterialTheme.typography.bodySmall)
                            Text(formatPrecio(anuncio.condicion.moneda, anuncio.condicion.obtenerPrecioReferencia()), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                if (anuncio.estado is Publicado) {
                                    TextButton(onClick = {
                                        anuncio.pausar()
                                        estado.notificarCambio()
                                        estado.mostrarMensaje("Anuncio pausado por moderación")
                                    }) {
                                        Text("Pausar")
                                    }
                                }
                                if (anuncio.estado is Pausado) {
                                    TextButton(onClick = {
                                        anuncio.publicar()
                                        estado.notificarCambio()
                                        estado.mostrarMensaje("Anuncio reactivado")
                                    }) {
                                        Text("Reactivar")
                                    }
                                }
                                if (anuncio.estado !is Cerrado) {
                                    TextButton(
                                        onClick = {
                                            anuncio.cerrar()
                                            estado.notificarCambio()
                                            estado.mostrarMensaje("Anuncio cerrado por moderación")
                                        },
                                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Text("Cerrar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminModeracionScreenPreview() {
    InakiTheme {
        val estado = remember { EstadoApp() }
        AdminModeracionScreen(estado = estado)
    }
}
