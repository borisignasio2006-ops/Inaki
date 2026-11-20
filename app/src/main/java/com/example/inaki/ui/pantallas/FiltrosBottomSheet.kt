package com.example.inaki.ui.pantallas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inaki.dominio.enums.TipoInmueble
import com.example.inaki.dominio.filtros.*
import com.example.inaki.dominio.modelo.Zona
import com.example.inaki.ui.EstadoApp
import com.example.inaki.ui.theme.InakiTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosBottomSheet(
    zonas: List<Zona>,
    filtrosActuales: List<FiltroBusqueda>,
    onAplicar: (List<FiltroBusqueda>) -> Unit,
    onCerrar: () -> Unit
) {
    var minPrecioText by remember { mutableStateOf("") }
    var maxPrecioText by remember { mutableStateOf("") }
    var tipoInmuebleSeleccionado by remember { mutableStateOf<TipoInmueble?>(null) }
    var minDormitorios by remember { mutableIntStateOf(0) }
    var zonaElegidaIndex by remember { mutableIntStateOf(0) } // 0 means "Todas"

    ModalBottomSheet(
        onDismissRequest = onCerrar
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Filtrar Anuncios",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Zona Dropdown / Chips
            Text("Zona", fontWeight = FontWeight.SemiBold)
            var expandedZona by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expandedZona,
                onExpandedChange = { expandedZona = !expandedZona }
            ) {
                OutlinedTextField(
                    value = if (zonaElegidaIndex == 0) "Todas" else zonas[zonaElegidaIndex - 1].nombre,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedZona) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                )
                ExposedDropdownMenu(
                    expanded = expandedZona,
                    onDismissRequest = { expandedZona = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Todas") },
                        onClick = {
                            zonaElegidaIndex = 0
                            expandedZona = false
                        }
                    )
                    zonas.forEachIndexed { index, zona ->
                        DropdownMenuItem(
                            text = { Text(zona.nombre) },
                            onClick = {
                                zonaElegidaIndex = index + 1
                                expandedZona = false
                            }
                        )
                    }
                }
            }

            // Precio Min / Max
            Text("Rango de Precio", fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = minPrecioText,
                    onValueChange = { minPrecioText = it },
                    label = { Text("Mínimo") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = maxPrecioText,
                    onValueChange = { maxPrecioText = it },
                    label = { Text("Máximo") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }

            // Tipo Inmueble
            Text("Tipo de Inmueble", fontWeight = FontWeight.SemiBold)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TipoInmueble.entries.chunked(3).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowItems.forEach { tipo ->
                            FilterChip(
                                selected = tipoInmuebleSeleccionado == tipo,
                                onClick = {
                                    tipoInmuebleSeleccionado = if (tipoInmuebleSeleccionado == tipo) null else tipo
                                },
                                label = { Text(tipo.name, fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(3 - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Dormitorios mínimos
            Text("Dormitorios mínimos: $minDormitorios", fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = { if (minDormitorios > 0) minDormitorios-- }) {
                    Text("-")
                }
                Spacer(modifier = Modifier.width(24.dp))
                Text("$minDormitorios", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(24.dp))
                Button(onClick = { minDormitorios++ }) {
                    Text("+")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onAplicar(emptyList())
                        onCerrar()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Limpiar")
                }
                Button(
                    onClick = {
                        val listaFiltros = mutableListOf<FiltroBusqueda>()
                        if (zonaElegidaIndex > 0) {
                            val zId = zonas[zonaElegidaIndex - 1].id
                            listaFiltros.add(FiltroZona(zId))
                        }
                        val pMin = minPrecioText.toDoubleOrNull()
                        val pMax = maxPrecioText.toDoubleOrNull()
                        if (pMin != null || pMax != null) {
                            listaFiltros.add(FiltroPrecio(pMin ?: 0.0, pMax ?: Double.MAX_VALUE))
                        }
                        tipoInmuebleSeleccionado?.let {
                            listaFiltros.add(FiltroTipoInmueble(it))
                        }
                        if (minDormitorios > 0) {
                            listaFiltros.add(FiltroDormitorios(minDormitorios))
                        }
                        onAplicar(listaFiltros)
                        onCerrar()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Aplicar")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FiltrosBottomSheetPreview() {
    InakiTheme {
        val estado = remember { EstadoApp() }
        FiltrosBottomSheet(
            zonas = estado.zonas,
            filtrosActuales = emptyList(),
            onAplicar = {},
            onCerrar = {}
        )
    }
}
