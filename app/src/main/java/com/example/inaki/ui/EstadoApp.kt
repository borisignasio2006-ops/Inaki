package com.example.inaki.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inaki.datos.DatosDemo
import com.example.inaki.dominio.enums.TipoAnunciante
import com.example.inaki.dominio.modelo.*
import com.example.inaki.ui.navegacion.Pantalla
import java.util.Date

class EstadoApp : ViewModel() {

    val zonas = mutableStateListOf<Zona>()
    val planes = mutableStateListOf<PlanPublicacion>()
    val usuarios = mutableStateListOf<Usuario>()
    val anuncios = mutableStateListOf<Anuncio>()

    var usuarioActual by mutableStateOf<Usuario?>(null)
    var modoActivo by mutableStateOf(ModoApp.BUSCADOR)
    var pantallaActual by mutableStateOf<Pantalla>(Pantalla.Login)
    var revision by mutableIntStateOf(0)
    var snackbarMensaje by mutableStateOf<String?>(null)

    private var contadorId = 1000

    init {
        val zList = DatosDemo.generarZonas()
        val pList = DatosDemo.generarPlanes()
        val (uList, aList) = DatosDemo.generarUsuarios(zList, pList)

        zonas.addAll(zList)
        planes.addAll(pList)
        usuarios.addAll(uList)
        anuncios.addAll(aList)
    }

    fun siguienteId(): Int = ++contadorId

    fun mostrarMensaje(mensaje: String) {
        snackbarMensaje = mensaje
    }

    fun limpiarMensaje() {
        snackbarMensaje = null
    }

    fun hashear(clave: String): String = "demo#$clave"

    fun iniciarSesion(correo: String, clave: String): Boolean {
        val hash = hashear(clave)
        val encontrado = usuarios.find { it.correo.equals(correo.trim(), ignoreCase = true) && it.credencial.contrasenaHash == hash }
        if (encontrado != null) {
            usuarioActual = encontrado
            modoActivo = ModoApp.BUSCADOR
            pantallaActual = Pantalla.Main
            mostrarMensaje("Bienvenido, ${encontrado.nombre}")
            return true
        } else {
            mostrarMensaje("Correo o contraseña incorrectos")
            return false
        }
    }

    fun registrarUsuario(
        nombre: String,
        correo: String,
        telefono: String,
        clave: String,
        quierePublicar: Boolean,
        tipoAnunciante: TipoAnunciante?
    ): Boolean {
        if (nombre.isBlank() || correo.isBlank() || clave.isBlank()) {
            mostrarMensaje("Por favor complete los campos obligatorios")
            return false
        }
        if (usuarios.any { it.correo.equals(correo.trim(), ignoreCase = true) }) {
            mostrarMensaje("El correo ya está registrado")
            return false
        }

        val nuevoU = Usuario(
            id = siguienteId(),
            nombre = nombre.trim(),
            correo = correo.trim(),
            telefono = telefono.trim(),
            fechaRegistro = Date(),
            credencial = Credencial(hashear(clave), Date())
        ).apply {
            roles.add(RolBuscador())
            if (quierePublicar && tipoAnunciante != null) {
                val planGratis = planes.firstOrNull { it.precioMensual == 0.0 } ?: planes.first()
                val rolAnun = RolAnunciante(tipoAnunciante, false).apply {
                    plan = planGratis
                }
                roles.add(rolAnun)
            }
        }

        usuarios.add(nuevoU)
        usuarioActual = nuevoU
        modoActivo = ModoApp.BUSCADOR
        pantallaActual = Pantalla.Main
        mostrarMensaje("Cuenta creada con éxito")
        return true
    }

    fun cerrarSesion() {
        usuarioActual = null
        modoActivo = ModoApp.BUSCADOR
        pantallaActual = Pantalla.Login
        mostrarMensaje("Sesión cerrada")
    }

    fun cambiarModo(nuevoModo: ModoApp) {
        val u = usuarioActual ?: return
        val tienePermiso = when (nuevoModo) {
            ModoApp.BUSCADOR -> u.roles.any { it.tienePermiso("buscar") }
            ModoApp.ANUNCIANTE -> u.roles.any { it.tienePermiso("publicar_anuncio") }
            ModoApp.ADMINISTRADOR -> u.roles.any { it.tienePermiso("moderar_anuncio") }
        }
        if (tienePermiso) {
            modoActivo = nuevoModo
            mostrarMensaje("Modo cambiado a $nuevoModo")
        } else {
            mostrarMensaje("No tiene permisos para el modo $nuevoModo")
        }
    }

    fun notificarCambio() {
        revision++
    }
}
