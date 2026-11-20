package com.example.inaki.ui.pantallas

import androidx.compose.foundation.clickable
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
import com.example.inaki.dominio.enums.TipoOperacion
import com.example.inaki.dominio.modelo.AlertaBusqueda
import com.example.inaki.dominio.modelo.Anuncio
import com.example.inaki.dominio.modelo.Publicado
import com.example.inaki.dominio.modelo.Zona
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.navegacion.Pantalla

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasScreen(estado: EstadoApp) {
    val usuario = estado.usuarioActual
    val alertas = usuario?.alertas ?: emptyList()
    estado.revision

    var mostrarDialogNueva by remember { mutableStateOf(false) }
    var alertaTestSeleccionada by remember { mutableStateOf<AlertaBusqueda?>(null) }
    var resultadosTest by remember { mutableStateOf<List<Anuncio>>(emptyList()) }

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
                text = "Mis Alertas de Búsqueda",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            val tienePermiso = usuario?.roles?.any { it.tienePermiso("crear_alerta") } ?: false
            if (tienePermiso) {
                Button(onClick = { mostrarDialogNueva = true }) {
                    Text("+ Nueva Alerta")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (alertas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes alertas configuradas", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(alertas) { alerta ->
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
                                    text = "Operación: ${alerta.tipoOperacion}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Switch(
                                    checked = alerta.activa,
                                    onCheckedChange = {
                                        alerta.activa = it
                                        estado.notificarCambio()
                                    }
                                )
                            }
                            Text("Rango: ${alerta.precioMin} - ${alerta.precioMax}")
                            Text("Zona: ${alerta.zona?.nombre ?: "Todas las zonas"}")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = {
                                    resultadosTest = estado.anuncios.filter { it.estado is Publicado && alerta.coincideCon(it) }
                                    alertaTestSeleccionada = alerta
                                }) {
                                    Text("Probar alerta")
                                }
                                TextButton(
                                    onClick = {
                                        usuario?.alertas?.remove(alerta)
                                        estado.notificarCambio()
                                        estado.mostrarMensaje("Alerta eliminada")
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Eliminar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogNueva) {
        var tipoOpSelected by remember { mutableStateOf(TipoOperacion.ALQUILER) }
        var minText by remember { mutableStateOf("0") }
        var maxText by remember { mutableStateOf("5000") }
        var zonaSeleccionada by remember { mutableStateOf<Zona?>(null) }

        AlertDialog(
            onDismissRequest = { mostrarDialogNueva = false },
            title = { Text("Nueva Alerta de Búsqueda") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tipo de Operación:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TipoOperacion.entries.forEach { op ->
                            FilterChip(
                                selected = tipoOpSelected == op,
                                onClick = { tipoOpSelected = op },
                                label = { Text(op.name, fontSize = 10.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = minText,
                        onValueChange = { minText = it },
                        label = { Text("Precio Mínimo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = maxText,
                        onValueChange = { maxText = it },
                        label = { Text("Precio Máximo") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Zona:")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var expandedZona by remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { expandedZona = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(zonaSeleccionada?.nombre ?: "Todas las zonas")
                        }
                        DropdownMenu(
                            expanded = expandedZona,
                            onDismissRequest = { expandedZona = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Todas las zonas") },
                                onClick = {
                                    zonaSeleccionada = null
                                    expandedZona = false
                                }
                            )
                            estado.zonas.forEach { z ->
                                DropdownMenuItem(
                                    text = { Text(z.nombre) },
                                    onClick = {
                                        zonaSeleccionada = z
                                        expandedZona = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val min = minText.toDoubleOrNull() ?: 0.0
                    val max = maxText.toDoubleOrNull() ?: 999999.0
                    val nueva = AlertaBusqueda(estado.siguienteId(), tipoOpSelected, min, max, true).apply {
                        zona = zonaSeleccionada
                    }
                    usuario?.alertas?.add(nueva)
                    estado.notificarCambio()
                    estado.mostrarMensaje("Alerta creada con éxito")
                    mostrarDialogNueva = false
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogNueva = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (alertaTestSeleccionada != null) {
        AlertDialog(
            onDismissRequest = { alertaTestSeleccionada = null },
            title = { Text("Resultados de la Alerta (${resultadosTest.size} coincidencias)") },
            text = {
                if (resultadosTest.isEmpty()) {
                    Text("No hay anuncios publicados que coincidan con esta alerta actualmente.")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(resultadosTest) { anuncio ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        alertaTestSeleccionada = null
                                        estado.pantallaActual = Pantalla.DetalleAnuncio(anuncio.id)
                                    }
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(anuncio.titulo, fontWeight = FontWeight.Bold, maxLines = 1)
                                    Text(anuncio.inmueble.ubicacion.zona.nombre, style = MaterialTheme.typography.bodySmall)
                                    Text(formatPrecio(anuncio.condicion.moneda, anuncio.condicion.obtenerPrecioReferencia()), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { alertaTestSeleccionada = null }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
