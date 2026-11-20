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
import com.example.inaki.dominio.abstracciones.PasarelaQR
import com.example.inaki.dominio.abstracciones.PasarelaTarjeta
import com.example.inaki.dominio.enums.Moneda
import com.example.inaki.dominio.modelo.PlanPublicacion
import com.example.inaki.dominio.modelo.RolAnunciante
import com.example.inaki.ui.EstadoApp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanesScreen(estado: EstadoApp) {
    val usuario = estado.usuarioActual ?: return
    estado.revision
    val rolAnun = usuario.roles.filterIsInstance<RolAnunciante>().firstOrNull()

    var planSeleccionadoParaContratar by remember { mutableStateOf<PlanPublicacion?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Planes de Publicación",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text("Potencia tus anuncios y llega a más clientes en La Paz y Bolivia.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(estado.planes) { plan ->
                val esPlanActual = rolAnun?.plan?.id == plan.id

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (esPlanActual) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
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
                            Text(text = plan.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            if (plan.destacado) {
                                Surface(
                                    color = MaterialTheme.colorScheme.tertiaryContainer,
                                    shape = MaterialTheme.shapes.extraSmall
                                ) {
                                    Text("⭐ Destacado", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text("Precio mensual: ${formatPrecio(Moneda.BOB, plan.precioMensual)}", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        Text("Anuncios simultáneos permitidos: ${plan.maxAnuncios}")

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (esPlanActual) {
                                AssistChip(onClick = {}, label = { Text("Plan Actual") })
                            } else {
                                Button(onClick = { planSeleccionadoParaContratar = plan }) {
                                    Text("Contratar Plan")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (planSeleccionadoParaContratar != null) {
        val plan = planSeleccionadoParaContratar!!
        var medioPago by remember { mutableStateOf("QR") }

        AlertDialog(
            onDismissRequest = { planSeleccionadoParaContratar = null },
            title = { Text("Contratar Plan: ${plan.nombre}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Total a pagar: ${formatPrecio(Moneda.BOB, plan.precioMensual)}")
                    Text("Seleccione método de pago:")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = medioPago == "QR",
                            onClick = { medioPago = "QR" },
                            label = { Text("Pago QR") }
                        )
                        FilterChip(
                            selected = medioPago == "TARJETA",
                            onClick = { medioPago = "TARJETA" },
                            label = { Text("Tarjeta") }
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val exito = if (medioPago == "QR") {
                        PasarelaQR().cobrar(plan.precioMensual, Moneda.BOB, medioPago)
                    } else {
                        PasarelaTarjeta().cobrar(plan.precioMensual, Moneda.BOB, medioPago)
                    }

                    if (exito && rolAnun != null) {
                        rolAnun.plan = plan
                        estado.notificarCambio()
                        estado.mostrarMensaje("Plan contratado con éxito (${plan.nombre})")
                    } else {
                        estado.mostrarMensaje("Error al procesar el pago")
                    }
                    planSeleccionadoParaContratar = null
                }) {
                    Text("Pagar y Contratar")
                }
            },
            dismissButton = {
                TextButton(onClick = { planSeleccionadoParaContratar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
