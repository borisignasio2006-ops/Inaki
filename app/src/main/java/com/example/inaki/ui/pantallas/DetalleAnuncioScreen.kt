package com.example.inaki.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.dominio.abstracciones.NotificadorEmail
import com.example.inaki.dominio.abstracciones.NotificadorPush
import com.example.inaki.dominio.abstracciones.NotificadorWhatsApp
import com.example.inaki.dominio.enums.CanalContacto
import com.example.inaki.dominio.enums.EstadoConsulta
import com.example.inaki.dominio.enums.EstadoReporte
import com.example.inaki.dominio.enums.EstadoVisita
import com.example.inaki.dominio.enums.MotivoReporte
import com.example.inaki.dominio.modelo.*
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.navegacion.Pantalla
import com.example.inaki.ui.theme.InakiTheme
import java.util.Calendar
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
    val esPropietario = usuario?.anuncios?.any { it.id == anuncio.id } == true
    var horizonteMeses by remember { mutableFloatStateOf(12f) }

    // Dialog states
    var mostrarConsultarDialog by remember { mutableStateOf(false) }
    var mostrarVisitaDialog by remember { mutableStateOf(false) }
    var mostrarReportarDialog by remember { mutableStateOf(false) }

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
                    if (usuario != null && !esPropietario) {
                        IconButton(onClick = {
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
                        }) {
                            Text(if (esFavorito) "❤️" else "🤍", fontSize = 20.sp)
                        }
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

            if (!esPropietario && usuario != null) {
                val tieneConsultar = usuario.roles.any { it.tienePermiso("enviar_consulta") }
                val tieneVisita = usuario.roles.any { it.tienePermiso("solicitar_visita") }
                val tieneReportar = usuario.roles.any { it.tienePermiso("reportar") }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (tieneConsultar) {
                        Button(
                            onClick = { mostrarConsultarDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Consultar", fontSize = 12.sp)
                        }
                    }
                    if (tieneVisita) {
                        Button(
                            onClick = {
                                val tieneConsulta = usuario.consultas.any { it.anuncio.id == anuncio.id }
                                if (!tieneConsulta) {
                                    estado.mostrarMensaje("Debe enviar una consulta primero")
                                    mostrarConsultarDialog = true
                                } else {
                                    mostrarVisitaDialog = true
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Visita", fontSize = 12.sp)
                        }
                    }
                    if (tieneReportar) {
                        OutlinedButton(
                            onClick = { mostrarReportarDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reportar", fontSize = 12.sp)
                        }
                    }
                }
            } else if (esPropietario) {
                Text(
                    text = "Este es tu propio anuncio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- DIALOGO CONSULTAR ---
    if (mostrarConsultarDialog) {
        var mensajeTexto by remember { mutableStateOf("") }
        var canalSeleccionado by remember { mutableStateOf(CanalContacto.APP) }

        AlertDialog(
            onDismissRequest = { mostrarConsultarDialog = false },
            title = { Text("Enviar Consulta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Canal de contacto:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        CanalContacto.entries.forEach { canal ->
                            FilterChip(
                                selected = canalSeleccionado == canal,
                                onClick = { canalSeleccionado = canal },
                                label = { Text(canal.name, fontSize = 9.sp) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = mensajeTexto,
                        onValueChange = { mensajeTexto = it },
                        label = { Text("Mensaje al anunciante") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (mensajeTexto.isBlank()) {
                        estado.mostrarMensaje("El mensaje no puede estar vacío")
                        return@Button
                    }
                    val nuevaConsulta = Consulta(
                        id = estado.siguienteId(),
                        mensaje = mensajeTexto.trim(),
                        canal = canalSeleccionado,
                        estado = EstadoConsulta.PENDIENTE,
                        fecha = Date(),
                        anuncio = anuncio
                    )
                    usuario?.consultas?.add(nuevaConsulta)

                    val propietarioCorreo = estado.usuarios.find { u -> u.anuncios.any { it.id == anuncio.id } }?.correo ?: "propietario@inaki.bo"
                    when (canalSeleccionado) {
                        CanalContacto.EMAIL -> NotificadorEmail().enviar(propietarioCorreo, mensajeTexto)
                        CanalContacto.WHATSAPP -> NotificadorWhatsApp().enviar(propietarioCorreo, mensajeTexto)
                        else -> NotificadorPush().enviar(propietarioCorreo, mensajeTexto)
                    }

                    estado.notificarCambio()
                    estado.mostrarMensaje("Consulta enviada con éxito")
                    mostrarConsultarDialog = false
                }) {
                    Text("Enviar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConsultarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- DIALOGO VISITA ---
    if (mostrarVisitaDialog) {
        val cal = Calendar.getInstance()
        val diasDisponibles = (1..7).map {
            cal.time.also { cal.add(Calendar.DAY_OF_MONTH, 1) }
        }
        var diaSeleccionado by remember { mutableStateOf(diasDisponibles.first()) }
        val horasDisponibles = listOf("09:00", "11:00", "15:00", "17:00")
        var horaSeleccionada by remember { mutableStateOf(horasDisponibles.first()) }

        AlertDialog(
            onDismissRequest = { mostrarVisitaDialog = false },
            title = { Text("Solicitar Visita") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Seleccione día:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        diasDisponibles.take(4).forEach { d ->
                            val label = android.text.format.DateFormat.format("dd/MM", d).toString()
                            FilterChip(
                                selected = diaSeleccionado == d,
                                onClick = { diaSeleccionado = d },
                                label = { Text(label, fontSize = 10.sp) }
                            )
                        }
                    }
                    Text("Seleccione hora:")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        horasDisponibles.forEach { h ->
                            FilterChip(
                                selected = horaSeleccionada == h,
                                onClick = { horaSeleccionada = h },
                                label = { Text(h, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val consultaPropia = usuario?.consultas?.firstOrNull { it.anuncio.id == anuncio.id }
                    if (consultaPropia == null) {
                        estado.mostrarMensaje("Debe enviar una consulta primero")
                        mostrarVisitaDialog = false
                        mostrarConsultarDialog = true
                        return@Button
                    }
                    val parts = horaSeleccionada.split(":")
                    val hora = parts[0].toInt()
                    val minuto = parts[1].toInt()
                    val calFinal = Calendar.getInstance().apply {
                        time = diaSeleccionado
                        set(Calendar.HOUR_OF_DAY, hora)
                        set(Calendar.MINUTE, minuto)
                    }

                    val nuevaVisita = Visita(
                        id = estado.siguienteId(),
                        fechaHora = calFinal.time,
                        estado = EstadoVisita.SOLICITADA
                    )
                    consultaPropia.visitas.add(nuevaVisita)
                    estado.notificarCambio()
                    estado.mostrarMensaje("Visita solicitada con éxito")
                    mostrarVisitaDialog = false
                }) {
                    Text("Confirmar Visita")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarVisitaDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- DIALOGO REPORTAR ---
    if (mostrarReportarDialog) {
        var motivoSeleccionado by remember { mutableStateOf(MotivoReporte.INFORMACION_FALSA) }
        var descripcionReporte by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { mostrarReportarDialog = false },
            title = { Text("Reportar Anuncio") },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Seleccione motivo:")
                    MotivoReporte.entries.forEach { motivo ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { motivoSeleccionado = motivo }
                        ) {
                            RadioButton(
                                selected = motivoSeleccionado == motivo,
                                onClick = { motivoSeleccionado = motivo }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(motivo.name, fontSize = 13.sp)
                        }
                    }
                    OutlinedTextField(
                        value = descripcionReporte,
                        onValueChange = { descripcionReporte = it },
                        label = { Text("Descripción adicional (opcional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val yaReportado = usuario?.reportes?.any { it.anuncio.id == anuncio.id && it.motivo == motivoSeleccionado } == true
                    if (yaReportado) {
                        estado.mostrarMensaje("Ya ha reportado este anuncio con este motivo")
                        return@Button
                    }
                    val nuevoReporte = Reporte(
                        id = estado.siguienteId(),
                        motivo = motivoSeleccionado,
                        descripcion = descripcionReporte.trim(),
                        estado = EstadoReporte.ABIERTO,
                        fecha = Date(),
                        anuncio = anuncio
                    )
                    usuario?.reportes?.add(nuevoReporte)
                    estado.notificarCambio()
                    estado.mostrarMensaje("Reporte enviado con éxito")
                    mostrarReportarDialog = false
                }) {
                    Text("Enviar Reporte")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarReportarDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DetalleAnuncioScreenPreview() {
    InakiTheme {
        val estado = remember { EstadoApp().apply { usuarioActual = usuarios.firstOrNull() } }
        val primerAnuncioId = estado.anuncios.firstOrNull()?.id ?: 1
        DetalleAnuncioScreen(estado = estado, anuncioId = primerAnuncioId)
    }
}
