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
import com.example.inaki.dominio.enums.EstadoReporte
import com.example.inaki.dominio.modelo.Reporte
import com.example.inaki.ui.EstadoApp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AdminReportesScreen(estado: EstadoApp) {
    estado.revision
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-BO"))

    val reportes = estado.usuarios.flatMap { u -> u.reportes.map { Pair(u, it) } }
        .filter { it.second.estado == EstadoReporte.ABIERTO || it.second.estado == EstadoReporte.EN_REVISION }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gestión de Reportes",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (reportes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay reportes abiertos", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reportes) { pair ->
                    val usuario = pair.first
                    val reporte = pair.second

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
                                    text = "Motivo: ${reporte.motivo}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                AssistChip(
                                    onClick = {},
                                    label = { Text(reporte.estado.name, fontSize = 10.sp) }
                                )
                            }
                            Text("Anuncio: ${reporte.anuncio.titulo}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Reportado por: ${usuario.nombre}", style = MaterialTheme.typography.bodySmall)
                            Text("Descripción: \"${reporte.descripcion}\"", style = MaterialTheme.typography.bodySmall)
                            Text("Fecha: ${dateFormat.format(reporte.fecha)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (reporte.estado == EstadoReporte.ABIERTO) {
                                    TextButton(onClick = {
                                        reporte.estado = EstadoReporte.EN_REVISION
                                        estado.notificarCambio()
                                        estado.mostrarMensaje("Reporte tomado en revisión")
                                    }) {
                                        Text("Tomar")
                                    }
                                }
                                TextButton(
                                    onClick = {
                                        reporte.estado = EstadoReporte.DESCARTADO
                                        estado.notificarCambio()
                                        estado.mostrarMensaje("Reporte descartado")
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Descartar")
                                }
                                Button(onClick = {
                                    reporte.estado = EstadoReporte.RESUELTO
                                    reporte.anuncio.cerrar()
                                    estado.notificarCambio()
                                    estado.mostrarMensaje("Reporte resuelto y anuncio cerrado")
                                }) {
                                    Text("Resolver y cerrar anuncio")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
