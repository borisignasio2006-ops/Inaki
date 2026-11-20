package com.example.inaki.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.dominio.enums.TipoOperacion
import com.example.inaki.dominio.filtros.FiltroBusqueda
import com.example.inaki.dominio.modelo.Anuncio
import com.example.inaki.dominio.modelo.Favorito
import com.example.inaki.dominio.modelo.Publicado
import com.example.inaki.dominio.modelo.RolAnunciante
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.navegacion.Pantalla
import com.example.inaki.ui.theme.InakiTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorarScreen(estado: EstadoApp) {
    var textoBusqueda by remember { mutableStateOf("") }
    var operacionSeleccionada by remember { mutableStateOf<TipoOperacion?>(null) }
    var verMapa by remember { mutableStateOf(false) }
    var mostrarFiltrosSheet by remember { mutableStateOf(false) }
    var filtrosActivos by remember { mutableStateOf<List<FiltroBusqueda>>(emptyList()) }
    var anuncioSeleccionadoMapa by remember { mutableStateOf<Anuncio?>(null) }

    estado.revision

    val anunciosPublicados = estado.anuncios.filter { it.estado is Publicado }

    val anunciosFiltrados = anunciosPublicados.filter { anuncio ->
        val coincideTexto = textoBusqueda.isBlank() ||
                anuncio.titulo.contains(textoBusqueda, ignoreCase = true) ||
                anuncio.inmueble.ubicacion.zona.nombre.contains(textoBusqueda, ignoreCase = true) ||
                anuncio.inmueble.ubicacion.direccion.contains(textoBusqueda, ignoreCase = true)

        val coincideOperacion = operacionSeleccionada == null || anuncio.condicion.tipo() == operacionSeleccionada
        val cumpleFiltros = filtrosActivos.all { it.cumple(anuncio) }

        coincideTexto && coincideOperacion && cumpleFiltros
    }

    val anunciosOrdenados = anunciosFiltrados.sortedByDescending { anuncio ->
        val duenho = estado.usuarios.find { u -> u.anuncios.any { it.id == anuncio.id } }
        val esDestacado = duenho?.roles?.filterIsInstance<RolAnunciante>()?.any { it.plan?.destacado == true } == true
        if (esDestacado) 1 else 0
    }

    val usuario = estado.usuarioActual

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = { Text("Buscar por título, zona...") },
                leadingIcon = { Text("🔍", fontSize = 16.sp) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            Button(onClick = { mostrarFiltrosSheet = true }) {
                val numFiltros = filtrosActivos.size
                Text(if (numFiltros > 0) "Filtros ($numFiltros)" else "Filtros")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = operacionSeleccionada == null,
                onClick = { operacionSeleccionada = null },
                label = { Text("Todos", fontSize = 11.sp) },
                modifier = Modifier.weight(1f)
            )
            TipoOperacion.entries.forEach { op ->
                FilterChip(
                    selected = operacionSeleccionada == op,
                    onClick = { operacionSeleccionada = if (operacionSeleccionada == op) null else op },
                    label = { Text(op.name, fontSize = 10.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${anunciosOrdenados.size} inmuebles encontrados",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = !verMapa,
                    onClick = { verMapa = false },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) {
                    Text("Lista")
                }
                SegmentedButton(
                    selected = verMapa,
                    onClick = { verMapa = true },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) {
                    Text("Mapa")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (verMapa) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val step = 50f
                    var x = 0f
                    while (x < w) {
                        drawLine(Color.LightGray, Offset(x, 0f), Offset(x, h), 1f)
                        x += step
                    }
                    var y = 0f
                    while (y < h) {
                        drawLine(Color.LightGray, Offset(0f, y), Offset(w, y), 1f)
                        y += step
                    }
                }

                val minLat = -16.56
                val maxLat = -16.48
                val minLon = -68.14
                val maxLon = -68.06

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val boxW = constraints.maxWidth.toFloat()
                    val boxH = constraints.maxHeight.toFloat()

                    anunciosOrdenados.forEach { anuncio ->
                        val lat = anuncio.inmueble.ubicacion.latitud
                        val lon = anuncio.inmueble.ubicacion.longitud
                        val aprox = anuncio.inmueble.ubicacion.esAproximada

                        val nx = ((lon - minLon) / (maxLon - minLon)).toFloat().coerceIn(0.05f, 0.95f)
                        val ny = 1f - ((lat - minLat) / (maxLat - minLat)).toFloat().coerceIn(0.05f, 0.95f)

                        val px = nx * boxW
                        val py = ny * boxH

                        Box(
                            modifier = Modifier
                                .offset(x = (px / 2.5f).dp, y = (py / 2.5f).dp)
                                .size(if (aprox) 36.dp else 24.dp)
                                .clip(CircleShape)
                                .background(if (aprox) MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary)
                                .clickable { anuncioSeleccionadoMapa = anuncio },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📍", fontSize = 12.sp)
                        }
                    }
                }

                if (anuncioSeleccionadoMapa != null) {
                    val sel = anuncioSeleccionadoMapa!!
                    Card(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(sel.titulo, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(formatPrecio(sel.condicion.moneda, sel.condicion.obtenerPrecioReferencia()), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            }
                            Button(onClick = {
                                estado.pantallaActual = Pantalla.DetalleAnuncio(sel.id)
                            }) {
                                Text("Ver detalle")
                            }
                        }
                    }
                }
            }
        } else {
            if (anunciosOrdenados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron anuncios con los filtros actuales.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(anunciosOrdenados) { anuncio ->
                        val duenho = estado.usuarios.find { u -> u.anuncios.any { it.id == anuncio.id } }
                        val rolAnun = duenho?.roles?.filterIsInstance<RolAnunciante>()?.firstOrNull()
                        val verificado = rolAnun?.verificado ?: false
                        val esDestacado = rolAnun?.plan?.destacado ?: false
                        val diasSinConfirmar = (System.currentTimeMillis() - anuncio.fechaUltimaConfirmacion.time) / (1000 * 60 * 60 * 24)
                        val sinConfirmar = diasSinConfirmar > 30

                        val esFavorito = usuario?.favoritos?.any { it.anuncio.id == anuncio.id } ?: false

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    estado.pantallaActual = Pantalla.DetalleAnuncio(anuncio.id)
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Foto 1", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Badge { Text(anuncio.condicion.tipo().name) }
                                            IconButton(
                                                onClick = {
                                                    if (usuario != null) {
                                                        if (esFavorito) {
                                                            val fav = usuario.favoritos.firstOrNull { it.anuncio.id == anuncio.id }
                                                            if (fav != null) usuario.favoritos.remove(fav)
                                                            estado.mostrarMensaje("Eliminado de favoritos")
                                                        } else {
                                                            val nuevoFav = Favorito(estado.siguienteId(), Date(), anuncio)
                                                            usuario.favoritos.add(nuevoFav)
                                                            estado.mostrarMensaje("Agregado a favoritos")
                                                        }
                                                        estado.notificarCambio()
                                                    }
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Text(if (esFavorito) "❤️" else "🤍", fontSize = 16.sp)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = anuncio.titulo,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = "${anuncio.inmueble.ubicacion.zona.nombre} • ${anuncio.inmueble.tipo}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = formatPrecio(anuncio.condicion.moneda, anuncio.condicion.obtenerPrecioReferencia()),
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 16.sp
                                            )

                                            Text(
                                                text = "${anuncio.inmueble.superficieM2.toInt()} m² | ${anuncio.inmueble.dormitorios} dorm",
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            if (esDestacado) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                                    shape = MaterialTheme.shapes.extraSmall
                                                ) {
                                                    Text("⭐ Destacado", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                }
                                            }
                                            if (verificado) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                                    shape = MaterialTheme.shapes.extraSmall
                                                ) {
                                                    Text("Verificado", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                                }
                                            }
                                            if (sinConfirmar) {
                                                Surface(
                                                    color = MaterialTheme.colorScheme.errorContainer,
                                                    shape = MaterialTheme.shapes.extraSmall
                                                ) {
                                                    Text("Sin confirmar", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
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

    if (mostrarFiltrosSheet) {
        FiltrosBottomSheet(
            zonas = estado.zonas,
            filtrosActuales = filtrosActivos,
            onAplicar = { nuevosFiltros ->
                filtrosActivos = nuevosFiltros
            },
            onCerrar = {
                mostrarFiltrosSheet = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ExplorarScreenPreview() {
    InakiTheme {
        val estado = remember { EstadoApp().apply { usuarioActual = usuarios.firstOrNull() } }
        ExplorarScreen(estado = estado)
    }
}
