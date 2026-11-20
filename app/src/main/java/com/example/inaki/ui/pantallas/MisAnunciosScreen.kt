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
import com.example.inaki.dominio.abstracciones.NotificadorPush
import com.example.inaki.dominio.modelo.*
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.theme.InakiTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisAnunciosScreen(estado: EstadoApp, onIrANuevoAnuncio: () -> Unit, onIrAPlanes: () -> Unit) {
    val usuario = estado.usuarioActual ?: return
    estado.revision
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-BO"))

    var anuncioParaCerrar by remember { mutableStateOf<Anuncio?>(null) }

    val rolAnun = usuario.roles.filterIsInstance<RolAnunciante>().firstOrNull()
    val maxAnuncios = rolAnun?.plan?.maxAnuncios ?: 1

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onIrANuevoAnuncio) {
                Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Mis Anuncios Publicados",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            val anuncios = usuario.anuncios
            if (anuncios.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No tienes anuncios creados", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onIrANuevoAnuncio) {
                            Text("Crear primer anuncio")
                        }
                    }
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
                        val consultasCount = estado.usuarios.flatMap { it.consultas }.count { it.anuncio.id == anuncio.id }

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
                                Text(formatPrecio(anuncio.condicion.moneda, anuncio.condicion.obtenerPrecioReferencia()), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                                Text("Consultas recibidas: $consultasCount", style = MaterialTheme.typography.bodySmall)
                                Text("Última confirmación: ${dateFormat.format(anuncio.fechaUltimaConfirmacion)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (anuncio.estado is Borrador || anuncio.estado is Pausado) {
                                        TextButton(onClick = {
                                            val pubCount = usuario.anuncios.count { it.estado is Publicado }
                                            if (pubCount >= maxAnuncios) {
                                                estado.mostrarMensaje("Límite de anuncios publicados alcanzado ($maxAnuncios). Actualice su plan.")
                                                onIrAPlanes()
                                                return@TextButton
                                            }
                                            anuncio.publicar()
                                            // Notificar alertas
                                            var countNotificados = 0
                                            estado.usuarios.forEach { u ->
                                                u.alertas.forEach { alerta ->
                                                    if (alerta.activa && alerta.coincideCon(anuncio)) {
                                                        NotificadorPush().enviar(u.correo, "Nuevo anuncio coincidente: ${anuncio.titulo}")
                                                        countNotificados++
                                                    }
                                                }
                                            }
                                            estado.notificarCambio()
                                            estado.mostrarMensaje("Anuncio publicado. Se notificó a $countNotificados usuarios.")
                                        }) {
                                            Text("Publicar")
                                        }
                                    }
                                    if (anuncio.estado is Publicado) {
                                        TextButton(onClick = {
                                            anuncio.pausar()
                                            estado.notificarCambio()
                                            estado.mostrarMensaje("Anuncio pausado")
                                        }) {
                                            Text("Pausar")
                                        }
                                        TextButton(onClick = {
                                            anuncio.fechaUltimaConfirmacion = Date()
                                            estado.notificarCambio()
                                            estado.mostrarMensaje("Disponibilidad confirmada")
                                        }) {
                                            Text("Confirmar")
                                        }
                                    }
                                    if (anuncio.estado !is Cerrado) {
                                        TextButton(
                                            onClick = { anuncioParaCerrar = anuncio },
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

    if (anuncioParaCerrar != null) {
        AlertDialog(
            onDismissRequest = { anuncioParaCerrar = null },
            title = { Text("Cerrar Anuncio") },
            text = { Text("¿Está seguro de cerrar este anuncio? Ya no aparecerá en las búsquedas.") },
            confirmButton = {
                Button(onClick = {
                    anuncioParaCerrar?.cerrar()
                    estado.notificarCambio()
                    estado.mostrarMensaje("Anuncio cerrado")
                    anuncioParaCerrar = null
                }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Cerrar Anuncio")
                }
            },
            dismissButton = {
                TextButton(onClick = { anuncioParaCerrar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MisAnunciosScreenPreview() {
    InakiTheme {
        val estado = remember { EstadoApp().apply { usuarioActual = usuarios.firstOrNull() } }
        MisAnunciosScreen(estado = estado, onIrANuevoAnuncio = {}, onIrAPlanes = {})
    }
}
