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
import com.example.inaki.dominio.abstracciones.NotificadorEmail
import com.example.inaki.dominio.enums.EstadoConsulta
import com.example.inaki.dominio.enums.EstadoVisita
import com.example.inaki.dominio.modelo.*
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.ModoApp
import com.example.inaki.ui.navegacion.Pantalla
import com.example.inaki.ui.theme.InakiTheme
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ConsultasScreen(estado: EstadoApp) {
    val usuario = estado.usuarioActual ?: return
    estado.revision
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es-BO"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (estado.modoActivo == ModoApp.BUSCADOR) "Mis Consultas" else "Consultas Recibidas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (estado.modoActivo == ModoApp.BUSCADOR) {
            val misConsultas = usuario.consultas
            if (misConsultas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No has enviado ninguna consulta", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(misConsultas) { consulta ->
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
                                        text = consulta.anuncio.titulo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(consulta.estado.name, fontSize = 10.sp) }
                                    )
                                }
                                Text("Canal: ${consulta.canal} | Fecha: ${dateFormat.format(consulta.fecha)}")
                                Text("Mensaje: \"${consulta.mensaje}\"", style = MaterialTheme.typography.bodyMedium)

                                if (consulta.visitas.isNotEmpty()) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    Text("Visitas Agendadas:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    consulta.visitas.forEach { visita ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Fecha: ${dateFormat.format(visita.fechaHora)}")
                                                Text("Estado: ${visita.estado}", style = MaterialTheme.typography.bodySmall)
                                            }
                                            if (visita.estado == EstadoVisita.SOLICITADA || visita.estado == EstadoVisita.CONFIRMADA) {
                                                TextButton(onClick = {
                                                    visita.cancelar()
                                                    estado.notificarCambio()
                                                    estado.mostrarMensaje("Visita cancelada")
                                                }) {
                                                    Text("Cancelar visita", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                                                }
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = {
                                        estado.pantallaActual = Pantalla.DetalleAnuncio(consulta.anuncio.id)
                                    }) {
                                        Text("Ver anuncio")
                                    }
                                    if (consulta.estado != EstadoConsulta.CERRADA) {
                                        TextButton(onClick = {
                                            consulta.estado = EstadoConsulta.CERRADA
                                            estado.notificarCambio()
                                            estado.mostrarMensaje("Consulta cerrada")
                                        }) {
                                            Text("Cerrar consulta")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            val misAnunciosIds = usuario.anuncios.map { it.id }.toSet()
            val consultasRecibidas = estado.usuarios.flatMap { it.consultas }.filter { it.anuncio.id in misAnunciosIds }

            if (consultasRecibidas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes consultas recibidas", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(consultasRecibidas) { consulta ->
                        val remitente = estado.usuarios.find { u -> u.consultas.any { c -> c.id == consulta.id } }

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
                                        text = consulta.anuncio.titulo,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1
                                    )
                                    AssistChip(
                                        onClick = {},
                                        label = { Text(consulta.estado.name, fontSize = 10.sp) }
                                    )
                                }
                                Text("De: ${remitente?.nombre ?: "Usuario"} (${remitente?.telefono ?: ""})")
                                Text("Canal: ${consulta.canal} | Fecha: ${dateFormat.format(consulta.fecha)}")
                                Text("Mensaje: \"${consulta.mensaje}\"", style = MaterialTheme.typography.bodyMedium)

                                if (consulta.estado != EstadoConsulta.CERRADA) {
                                    var textoRespuesta by remember { mutableStateOf("") }
                                    OutlinedTextField(
                                        value = textoRespuesta,
                                        onValueChange = { textoRespuesta = it },
                                        label = { Text("Escribir respuesta...") },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Button(
                                        onClick = {
                                            if (textoRespuesta.isNotBlank()) {
                                                consulta.estado = EstadoConsulta.RESPONDIDA
                                                NotificadorEmail().enviar(remitente?.correo ?: "usuario@inaki.bo", "Respuesta a consulta: $textoRespuesta")
                                                estado.notificarCambio()
                                                estado.mostrarMensaje("Respuesta enviada")
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Responder")
                                    }
                                }

                                if (consulta.visitas.isNotEmpty()) {
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    Text("Visitas Solicitadas:", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    consulta.visitas.forEach { visita ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Fecha: ${dateFormat.format(visita.fechaHora)}")
                                                Text("Estado: ${visita.estado}", style = MaterialTheme.typography.bodySmall)
                                            }
                                            if (visita.estado == EstadoVisita.SOLICITADA) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Button(onClick = {
                                                        visita.confirmar()
                                                        estado.notificarCambio()
                                                        estado.mostrarMensaje("Visita confirmada")
                                                    }) {
                                                        Text("Confirmar", fontSize = 11.sp)
                                                    }
                                                    OutlinedButton(onClick = {
                                                        visita.cancelar()
                                                        estado.notificarCambio()
                                                        estado.mostrarMensaje("Visita rechazada")
                                                    }) {
                                                        Text("Rechazar", fontSize = 11.sp)
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
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConsultasScreenPreview() {
    InakiTheme {
        val estado = remember { EstadoApp().apply { usuarioActual = usuarios.firstOrNull() } }
        ConsultasScreen(estado = estado)
    }
}
