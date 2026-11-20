package com.example.inaki.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.dominio.modelo.*
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.navegacion.Pantalla
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleAnuncioScreen(estado: EstadoApp, anuncioId: Int) {
    val anuncio = estado.anuncios.firstOrNull { it.id == anuncioId }
    val usuario = estado.usuarioActual

    if (anuncio == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Anuncio no encontrado")
        }
        return
    }

    val esFavorito = usuario?.favoritos?.any { it.anuncio.id == anuncio.id } ?: false
    var horizonteMeses by remember { mutableFloatStateOf(12f) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(anuncio.titulo, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { estado.pantallaActual = Pantalla.Main }) {
                        Text("❌", fontSize = 18.sp)
                    }
                },
                actions = {
                    IconButton(onClick = {
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
                    }) {
                        Text(if (esFavorito) "❤️" else "🤍", fontSize = 20.sp)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val fotos = anuncio.inmueble.fotos
            val pagerState = rememberPagerState(pageCount = { if (fotos.isEmpty()) 1 else fotos.size })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f * (page + 1))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (fotos.isNotEmpty()) "Foto ${page + 1} (${fotos[page].url})" else "Sin fotos",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Text(text = anuncio.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = "Zona: ${anuncio.inmueble.ubicacion.zona.nombre} | ${if (anuncio.inmueble.ubicacion.esAproximada) "Ubicación aproximada" else anuncio.inmueble.ubicacion.direccion}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ficha del Inmueble", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Tipo: ${anuncio.inmueble.tipo}")
                    Text("Superficie: ${anuncio.inmueble.superficieM2} m²")
                    Text("Dormitorios: ${anuncio.inmueble.dormitorios} | Baños: ${anuncio.inmueble.banos}")
                    Text("Amoblado: ${if (anuncio.inmueble.amoblado) "Sí" else "No"}")
                    Text("Acepta mascotas: ${if (anuncio.inmueble.aceptaMascotas) "Sí" else "No"}")
                    if (anuncio.inmueble.serviciosIncluidos.isNotBlank()) {
                        Text("Servicios incluidos: ${anuncio.inmueble.serviciosIncluidos}")
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Condiciones de Operación (${anuncio.condicion.tipo()})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    when (val cond = anuncio.condicion) {
                        is Alquiler -> {
                            Text("Precio Mensual: ${formatPrecio(cond.moneda, cond.precioMensual)}")
                            Text("Garantía: ${formatPrecio(cond.moneda, cond.garantia)}")
                            Text("Meses mínimos: ${cond.mesesMinimos}")

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Cálculo Costo Total (${horizonteMeses.toInt()} meses): ${formatPrecio(cond.moneda, cond.calcularCostoTotal(horizonteMeses.toInt()))}")
                            Slider(
                                value = horizonteMeses,
                                onValueChange = { horizonteMeses = it },
                                valueRange = 1f..36f,
                                steps = 35
                            )
                        }
                        is Venta -> {
                            Text("Precio Venta: ${formatPrecio(cond.moneda, cond.precio)}")
                            Text("Negociable: ${if (cond.negociable) "Sí" else "No"}")
                        }
                        is Anticretico -> {
                            Text("Monto Anticrético: ${formatPrecio(cond.moneda, cond.montoAnticretico)}")
                            Text("Plazo: ${cond.plazoMeses} meses")
                            Text("Capital inmovilizado: ${formatPrecio(cond.moneda, cond.calcularCapitalInmovilizado())}")
                        }
                    }
                    if (anuncio.condicion.condicionesExtra.isNotBlank()) {
                        Text("Condiciones extra: ${anuncio.condicion.condicionesExtra}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { estado.mostrarMensaje("Se activa en el Commit 3") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Consultar", fontSize = 12.sp)
                }
                Button(
                    onClick = { estado.mostrarMensaje("Se activa en el Commit 3") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Visita", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = { estado.mostrarMensaje("Se activa en el Commit 3") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reportar", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
