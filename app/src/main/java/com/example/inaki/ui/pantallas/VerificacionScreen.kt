package com.example.inaki.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.dominio.enums.EstadoVerificacion
import com.example.inaki.dominio.enums.TipoVerificacion
import com.example.inaki.dominio.modelo.Anuncio
import com.example.inaki.dominio.modelo.Verificacion
import com.example.inaki.ui.EstadoApp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerificacionScreen(estado: EstadoApp) {
    val usuario = estado.usuarioActual ?: return
    estado.revision
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-BO"))

    var mostrarDialogSolicitar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Verificaciones de Identidad y Propiedad",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = { mostrarDialogSolicitar = true }) {
                Text("+ Solicitar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val verificaciones = usuario.verificaciones
        if (verificaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes solicitudes de verificación", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(verificaciones) { verif ->
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
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Tipo: ${verif.tipo}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                AssistChip(
                                    onClick = {},
                                    label = { Text(verif.estado.name, fontSize = 10.sp) }
                                )
                            }
                            Text("Fecha: ${dateFormat.format(verif.fecha)}", style = MaterialTheme.typography.bodySmall)
                            Text("Evidencia: ${verif.evidenciaUrl}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogSolicitar) {
        var tipoSeleccionado by remember { mutableStateOf(TipoVerificacion.IDENTIDAD) }
        var anuncioSeleccionado by remember { mutableStateOf<Anuncio?>(usuario.anuncios.firstOrNull()) }
        var evidenciaUrl by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarDialogSolicitar = false },
            title = { Text("Solicitar Verificación") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tipo de Verificación:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TipoVerificacion.entries.forEach { tv ->
                            FilterChip(
                                selected = tipoSeleccionado == tv,
                                onClick = { tipoSeleccionado = tv },
                                label = { Text(tv.name, fontSize = 10.sp) }
                            )
                        }
                    }
                    if (tipoSeleccionado != TipoVerificacion.IDENTIDAD) {
                        Text("Anuncio asociado:")
                        Box(modifier = Modifier.fillMaxWidth()) {
                            var expandedAnuncio by remember { mutableStateOf(false) }
                            OutlinedButton(onClick = { expandedAnuncio = true }, modifier = Modifier.fillMaxWidth()) {
                                Text(anuncioSeleccionado?.titulo ?: "Seleccione un anuncio")
                            }
                            DropdownMenu(expanded = expandedAnuncio, onDismissRequest = { expandedAnuncio = false }) {
                                usuario.anuncios.forEach { a ->
                                    DropdownMenuItem(text = { Text(a.titulo) }, onClick = { anuncioSeleccionado = a; expandedAnuncio = false })
                                }
                            }
                        }
                    }
                    Button(
                        onClick = {
                            evidenciaUrl = "demo://evidencia/${estado.siguienteId()}"
                            estado.mostrarMensaje("Evidencia adjuntada correctamente")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (evidenciaUrl.isBlank()) "Adjuntar Evidencia (Simular)" else "Evidencia Adjunta OK")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (evidenciaUrl.isBlank()) {
                        evidenciaUrl = "demo://evidencia/${estado.siguienteId()}"
                    }
                    val nuevaVerif = Verificacion(
                        id = estado.siguienteId(),
                        tipo = tipoSeleccionado,
                        estado = EstadoVerificacion.PENDIENTE,
                        fecha = Date(),
                        evidenciaUrl = evidenciaUrl
                    )
                    usuario.verificaciones.add(nuevaVerif)
                    if (tipoSeleccionado != TipoVerificacion.IDENTIDAD && anuncioSeleccionado != null) {
                        anuncioSeleccionado!!.verificaciones.add(nuevaVerif)
                    }
                    estado.notificarCambio()
                    estado.mostrarMensaje("Solicitud de verificación enviada")
                    mostrarDialogSolicitar = false
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogSolicitar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
