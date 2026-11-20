package com.example.inaki.ui.pantallas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.dominio.abstracciones.NotificadorPush
import com.example.inaki.dominio.enums.Moneda
import com.example.inaki.dominio.enums.TipoInmueble
import com.example.inaki.dominio.enums.TipoOperacion
import com.example.inaki.dominio.modelo.*
import com.example.inaki.ui.EstadoApp
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsistenteAnuncioScreen(estado: EstadoApp, onVolver: () -> Unit) {
    val usuario = estado.usuarioActual ?: return
    val rolAnun = usuario.roles.filterIsInstance<RolAnunciante>().firstOrNull()
    val maxAnuncios = rolAnun?.plan?.maxAnuncios ?: 1

    var pasoActual by remember { mutableIntStateOf(1) }

    // Paso 1: Condicion de Operacion
    var tipoOperacion by remember { mutableStateOf(TipoOperacion.ALQUILER) }
    var moneda by remember { mutableStateOf(Moneda.BOB) }
    var precioMensualText by remember { mutableStateOf("3000") }
    var garantiaText by remember { mutableStateOf("3000") }
    var mesesMinimosText by remember { mutableStateOf("12") }
    var precioVentaText by remember { mutableStateOf("100000") }
    var negociableVenta by remember { mutableStateOf(true) }
    var montoAnticreticoText by remember { mutableStateOf("45000") }
    var plazoMesesText by remember { mutableStateOf("24") }
    var condicionesExtra by remember { mutableStateOf("") }

    // Paso 2: Inmueble
    var tipoInmueble by remember { mutableStateOf(TipoInmueble.DEPARTAMENTO) }
    var superficieText by remember { mutableStateOf("100") }
    var dormitoriosText by remember { mutableStateOf("2") }
    var banosText by remember { mutableStateOf("2") }
    var amoblado by remember { mutableStateOf(false) }
    var aceptaMascotas by remember { mutableStateOf(true) }
    var serviciosIncluidos by remember { mutableStateOf("Agua, luz") }

    // Paso 3: Ubicacion y Fotos
    var zonaSeleccionada by remember { mutableStateOf(estado.zonas.firstOrNull() ?: Zona(1, "Obrajes", "La Paz", "")) }
    var direccionText by remember { mutableStateOf("Av. Principal #123") }
    var latitudText by remember { mutableStateOf("-16.5000") }
    var longitudText by remember { mutableStateOf("-68.1000") }
    var esAproximada by remember { mutableStateOf(false) }
    val fotosList = remember { mutableStateListOf<Foto>(Foto(estado.siguienteId(), "demo://foto/1", 1, true)) }

    // Paso 4: Título y Descripción
    var tituloText by remember { mutableStateOf("") }
    var descripcionText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Anuncio (Paso $pasoActual de 4)") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Text("❌", fontSize = 18.sp)
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
            LinearProgressIndicator(
                progress = { pasoActual / 4f },
                modifier = Modifier.fillMaxWidth()
            )

            when (pasoActual) {
                1 -> {
                    Text("Paso 1: Condición de Operación", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        TipoOperacion.entries.forEach { op ->
                            FilterChip(
                                selected = tipoOperacion == op,
                                onClick = { tipoOperacion = op },
                                label = { Text(op.name, fontSize = 11.sp) }
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Moneda.entries.forEach { m ->
                            FilterChip(
                                selected = moneda == m,
                                onClick = { moneda = m },
                                label = { Text(m.name, fontSize = 11.sp) }
                            )
                        }
                    }

                    when (tipoOperacion) {
                        TipoOperacion.ALQUILER -> {
                            OutlinedTextField(value = precioMensualText, onValueChange = { precioMensualText = it }, label = { Text("Precio Mensual") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = garantiaText, onValueChange = { garantiaText = it }, label = { Text("Garantía") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = mesesMinimosText, onValueChange = { mesesMinimosText = it }, label = { Text("Meses Mínimos") }, modifier = Modifier.fillMaxWidth())
                        }
                        TipoOperacion.VENTA -> {
                            OutlinedTextField(value = precioVentaText, onValueChange = { precioVentaText = it }, label = { Text("Precio de Venta") }, modifier = Modifier.fillMaxWidth())
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = negociableVenta, onCheckedChange = { negociableVenta = it })
                                Text("Precio Negociable")
                            }
                        }
                        TipoOperacion.ANTICRETICO -> {
                            OutlinedTextField(value = montoAnticreticoText, onValueChange = { montoAnticreticoText = it }, label = { Text("Monto Anticrético") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = plazoMesesText, onValueChange = { plazoMesesText = it }, label = { Text("Plazo (meses)") }, modifier = Modifier.fillMaxWidth())
                        }
                    }
                    OutlinedTextField(value = condicionesExtra, onValueChange = { condicionesExtra = it }, label = { Text("Condiciones Extra (opcional)") }, modifier = Modifier.fillMaxWidth())
                }
                2 -> {
                    Text("Paso 2: Detalles del Inmueble", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Tipo de Inmueble:")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var expandedTipo by remember { mutableStateOf(false) }
                        OutlinedButton(onClick = { expandedTipo = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(tipoInmueble.name)
                        }
                        DropdownMenu(expanded = expandedTipo, onDismissRequest = { expandedTipo = false }) {
                            TipoInmueble.entries.forEach { ti ->
                                DropdownMenuItem(text = { Text(ti.name) }, onClick = { tipoInmueble = ti; expandedTipo = false })
                            }
                        }
                    }
                    OutlinedTextField(value = superficieText, onValueChange = { superficieText = it }, label = { Text("Superficie (m²)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dormitoriosText, onValueChange = { dormitoriosText = it }, label = { Text("Dormitorios") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = banosText, onValueChange = { banosText = it }, label = { Text("Baños") }, modifier = Modifier.fillMaxWidth())
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = amoblado, onCheckedChange = { amoblado = it })
                        Text("Amoblado")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = aceptaMascotas, onCheckedChange = { aceptaMascotas = it })
                        Text("Acepta Mascotas")
                    }
                    OutlinedTextField(value = serviciosIncluidos, onValueChange = { serviciosIncluidos = it }, label = { Text("Servicios Incluidos") }, modifier = Modifier.fillMaxWidth())
                }
                3 -> {
                    Text("Paso 3: Ubicación y Fotos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Zona:")
                    Box(modifier = Modifier.fillMaxWidth()) {
                        var expandedZona by remember { mutableStateOf(false) }
                        OutlinedButton(onClick = { expandedZona = true }, modifier = Modifier.fillMaxWidth()) {
                            Text(zonaSeleccionada.nombre)
                        }
                        DropdownMenu(expanded = expandedZona, onDismissRequest = { expandedZona = false }) {
                            estado.zonas.forEach { z ->
                                DropdownMenuItem(text = { Text(z.nombre) }, onClick = { zonaSeleccionada = z; expandedZona = false })
                            }
                        }
                    }
                    OutlinedTextField(value = direccionText, onValueChange = { direccionText = it }, label = { Text("Dirección Exacta") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = latitudText, onValueChange = { latitudText = it }, label = { Text("Latitud") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = longitudText, onValueChange = { longitudText = it }, label = { Text("Longitud") }, modifier = Modifier.fillMaxWidth())
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = esAproximada, onCheckedChange = { esAproximada = it })
                        Text("Ubicación aproximada (Privacidad)")
                    }

                    // Minimapa Canvas interactivo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small)
                            .clickable {
                                latitudText = (-16.5000 + (Math.random() * 0.04 - 0.02)).toString()
                                longituText = (-68.1000 + (Math.random() * 0.04 - 0.02)).toString()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawLine(Color.Gray, Offset(0f, size.height / 2), Offset(size.width, size.height / 2), 1f)
                            drawLine(Color.Gray, Offset(size.width / 2, 0f), Offset(size.width / 2, size.height), 1f)
                        }
                        Text("Toca el mapa para ajustar coordenadas", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Fotos (${fotosList.size}):", fontWeight = FontWeight.SemiBold)
                        Button(onClick = {
                            val nuevaFoto = Foto(estado.siguienteId(), "demo://foto/${fotosList.size + 1}", fotosList.size + 1, fotosList.isEmpty())
                            fotosList.add(nuevaFoto)
                        }) {
                            Text("+ Agregar Foto")
                        }
                    }
                    fotosList.forEachIndexed { index, foto ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Foto ${index + 1} ${if (foto.esPortada) "(Portada)" else ""}", fontSize = 12.sp)
                                Row {
                                    if (!foto.esPortada) {
                                        TextButton(onClick = {
                                            fotosList.forEach { it.esPortada = false }
                                            foto.esPortada = true
                                        }) { Text("Hacer portada", fontSize = 10.sp) }
                                    }
                                    TextButton(onClick = { fotosList.remove(foto) }) { Text("Eliminar", color = MaterialTheme.colorScheme.error, fontSize = 10.sp) }
                                }
                            }
                        }
                    }
                }
                4 -> {
                    Text("Paso 4: Título, Descripción y Resumen", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    OutlinedTextField(value = tituloText, onValueChange = { tituloText = it }, label = { Text("Título del Anuncio") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descripcionText, onValueChange = { descripcionText = it }, label = { Text("Descripción Detallada") }, modifier = Modifier.fillMaxWidth(), minLines = 3)

                    Spacer(modifier = Modifier.height(8.dp))
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Resumen:", fontWeight = FontWeight.Bold)
                            Text("Operación: $tipoOperacion ($moneda)")
                            Text("Inmueble: $tipoInmueble - $superficieText m²")
                            Text("Zona: ${zonaSeleccionada.nombre}")
                            Text("Fotos: ${fotosList.size}")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pasoActual > 1) {
                    OutlinedButton(onClick = { pasoActual-- }) {
                        Text("Anterior")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (pasoActual < 4) {
                    Button(onClick = {
                        if (pasoActual == 4 && tituloText.isBlank()) {
                            estado.mostrarMensaje("Ingrese un título")
                        } else {
                            pasoActual++
                        }
                    }) {
                        Text("Siguiente")
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            guardarAnuncio(estado, usuario, tipoOperacion, moneda, precioMensualText, garantiaText, mesesMinimosText, precioVentaText, negociableVenta, montoAnticreticoText, plazoMesesText, condicionesExtra, tipoInmueble, superficieText, dormitoriosText, banosText, amoblado, aceptaMascotas, serviciosIncluidos, zonaSeleccionada, direccionText, latitudText, longitudText, esAproximada, fotosList, tituloText, descripcionText, enBorrador = true)
                            onVolver()
                        }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                            Text("Guardar Borrador")
                        }
                        Button(onClick = {
                            val pubCount = usuario.anuncios.count { it.estado is Publicado }
                            if (pubCount >= maxAnuncios) {
                                estado.mostrarMensaje("Límite de anuncios publicados alcanzado ($maxAnuncios). Actualice su plan.")
                                return@Button
                            }
                            val nuevoAnuncio = guardarAnuncio(estado, usuario, tipoOperacion, moneda, precioMensualText, garantiaText, mesesMinimosText, precioVentaText, negociableVenta, montoAnticreticoText, plazoMesesText, condicionesExtra, tipoInmueble, superficieText, dormitoriosText, banosText, amoblado, aceptaMascotas, serviciosIncluidos, zonaSeleccionada, direccionText, latitudText, longitudText, esAproximada, fotosList, tituloText, descripcionText, enBorrador = false)

                            // Evaluar alertas
                            var countNotificados = 0
                            estado.usuarios.forEach { u ->
                                u.alertas.forEach { alerta ->
                                    if (alerta.activa && alerta.coincideCon(nuevoAnuncio)) {
                                        NotificadorPush().enviar(u.correo, "Nuevo anuncio coincidente: ${nuevoAnuncio.titulo}")
                                        countNotificados++
                                    }
                                }
                            }
                            estado.mostrarMensaje("Anuncio publicado con éxito. Se notificó a $countNotificados usuarios.")
                            onVolver()
                        }) {
                            Text("Publicar")
                        }
                    }
                }
            }
        }
    }
}

private var longituText: String = "-68.1000"

private fun guardarAnuncio(
    estado: EstadoApp,
    usuario: Usuario,
    tipoOperacion: TipoOperacion,
    moneda: Moneda,
    precioMensual: String,
    garantia: String,
    mesesMinimos: String,
    precioVenta: String,
    negociable: Boolean,
    montoAnticretico: String,
    plazoMeses: String,
    condExtra: String,
    tipoInmueble: TipoInmueble,
    superficie: String,
    dormitorios: String,
    banos: String,
    amoblado: Boolean,
    aceptaMascotas: Boolean,
    servicios: String,
    zona: Zona,
    direccion: String,
    lat: String,
    lon: String,
    aprox: Boolean,
    fotos: List<Foto>,
    titulo: String,
    descripcion: String,
    enBorrador: Boolean
): Anuncio {
    val condicion: CondicionOperacion = when (tipoOperacion) {
        TipoOperacion.ALQUILER -> Alquiler(moneda, condExtra, precioMensual.toDoubleOrNull() ?: 0.0, garantia.toDoubleOrNull() ?: 0.0, mesesMinimos.toIntOrNull() ?: 12)
        TipoOperacion.VENTA -> Venta(moneda, condExtra, precioVenta.toDoubleOrNull() ?: 0.0, negociable)
        TipoOperacion.ANTICRETICO -> Anticretico(moneda, condExtra, montoAnticretico.toDoubleOrNull() ?: 0.0, plazoMeses.toIntOrNull() ?: 24)
    }

    val inmuebleObj = Inmueble(
        id = estado.siguienteId(),
        tipo = tipoInmueble,
        superficieM2 = superficie.toDoubleOrNull() ?: 50.0,
        dormitorios = dormitorios.toIntOrNull() ?: 1,
        banos = banos.toIntOrNull() ?: 1,
        amoblado = amoblado,
        aceptaMascotas = aceptaMascotas,
        serviciosIncluidos = servicios,
        ubicacion = Ubicacion(lat.toDoubleOrNull() ?: -16.5, lon.toDoubleOrNull() ?: -68.1, direccion, aprox, zona)
    ).apply {
        this.fotos.addAll(fotos)
    }

    val anuncio = Anuncio(
        id = estado.siguienteId(),
        titulo = if (titulo.isBlank()) "Inmueble en ${zona.nombre}" else titulo,
        descripcion = if (descripcion.isBlank()) "Sin descripción detallada" else descripcion,
        fechaPublicacion = Date(),
        fechaUltimaConfirmacion = Date(),
        inmueble = inmuebleObj,
        condicion = condicion
    )

    if (!enBorrador) {
        anuncio.publicar()
    }

    usuario.anuncios.add(anuncio)
    estado.anuncios.add(anuncio)
    estado.notificarCambio()
    return anuncio
}
