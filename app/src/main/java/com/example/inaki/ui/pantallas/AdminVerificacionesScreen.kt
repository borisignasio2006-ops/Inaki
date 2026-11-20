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
import com.example.inaki.dominio.modelo.RolAnunciante
import com.example.inaki.ui.EstadoApp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AdminVerificacionesScreen(estado: EstadoApp) {
    estado.revision
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-BO"))

    val pendientes = estado.usuarios.flatMap { u -> u.verificaciones.map { Pair(u, it) } }
        .filter { it.second.estado == EstadoVerificacion.PENDIENTE }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Revisión de Verificaciones",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (pendientes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay solicitudes de verificación pendientes", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pendientes) { pair ->
                    val usuario = pair.first
                    val verif = pair.second

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
                                    text = "Usuario: ${usuario.nombre}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                AssistChip(
                                    onClick = {},
                                    label = { Text(verif.tipo.name, fontSize = 10.sp) }
                                )
                            }
                            Text("Fecha: ${dateFormat.format(verif.fecha)}", style = MaterialTheme.typography.bodySmall)
                            Text("Evidencia: ${verif.evidenciaUrl}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        verif.estado = EstadoVerificacion.RECHAZADA
                                        estado.notificarCambio()
                                        estado.mostrarMensaje("Verificación rechazada")
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Rechazar")
                                }
                                Button(onClick = {
                                    verif.estado = EstadoVerificacion.APROBADA
                                    if (verif.tipo == TipoVerificacion.IDENTIDAD) {
                                        usuario.roles.filterIsInstance<RolAnunciante>().forEach {
                                            it.verificado = true
                                        }
                                    }
                                    estado.notificarCambio()
                                    estado.mostrarMensaje("Verificación aprobada con éxito")
                                }) {
                                    Text("Aprobar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
