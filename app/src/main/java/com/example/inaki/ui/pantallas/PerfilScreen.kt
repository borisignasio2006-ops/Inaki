package com.example.inaki.ui.pantallas

import androidx.compose.foundation.layout.*
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
import com.example.inaki.ui.ModoApp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PerfilScreen(estado: EstadoApp) {
    val usuario = estado.usuarioActual ?: return
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "BO"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = usuario.nombre, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = "Correo: ${usuario.correo}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Teléfono: ${usuario.telefono}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Registro: ${dateFormat.format(usuario.fechaRegistro)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        Text(text = "Roles y Permisos", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            usuario.roles.forEach { rol ->
                val label = when (rol) {
                    is RolBuscador -> "Buscador"
                    is RolAnunciante -> "Anunciante (${rol.tipo}${if (rol.verificado) " - Verificado" else ""})"
                    is RolAdministrador -> "Administrador"
                    else -> "Rol"
                }
                AssistChip(
                    onClick = {},
                    label = { Text(label, fontSize = 11.sp) }
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(text = "Modo Activo", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModoApp.entries.forEach { modo ->
                val permite = when (modo) {
                    ModoApp.BUSCADOR -> usuario.roles.any { it.tienePermiso("buscar") }
                    ModoApp.ANUNCIANTE -> usuario.roles.any { it.tienePermiso("publicar_anuncio") }
                    ModoApp.ADMINISTRADOR -> usuario.roles.any { it.tienePermiso("moderar_anuncio") }
                }

                FilterChip(
                    selected = estado.modoActivo == modo,
                    onClick = {
                        if (permite) {
                            estado.cambiarModo(modo)
                        } else {
                            estado.mostrarMensaje("No tienes acceso a este modo")
                        }
                    },
                    label = { Text(modo.name, fontSize = 11.sp) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { estado.cerrarSesion() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Cerrar Sesión")
        }
    }
}
