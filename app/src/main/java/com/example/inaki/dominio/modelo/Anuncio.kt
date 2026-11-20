package com.example.inaki.dominio.modelo

import java.util.Date

class Anuncio(
    var id: Int,
    var titulo: String,
    var descripcion: String,
    var fechaPublicacion: Date,
    var fechaUltimaConfirmacion: Date,
    var inmueble: Inmueble,
    var condicion: CondicionOperacion
) {
    var estado: EstadoAnuncio = Borrador()
    val verificaciones: MutableList<Verificacion> = mutableListOf()

    fun publicar() {
        estado.publicar(this)
    }

    fun pausar() {
        estado.pausar(this)
    }

    fun cerrar() {
        estado.cerrar(this)
    }
}
