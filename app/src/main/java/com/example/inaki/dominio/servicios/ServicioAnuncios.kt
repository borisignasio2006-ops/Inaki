package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.IAlmacenamientoArchivos
import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.modelo.Anuncio

class ServicioAnuncios(
    private val repositorio: IRepositorio<Anuncio>,
    private val almacenamiento: IAlmacenamientoArchivos,
    private val servicioAlertas: ServicioAlertas
) {
    fun crear(datos: Map<String, String>): Anuncio {
        TODO("Fase de lógica")
    }

    fun confirmarDisponibilidad(id: Int) {
        TODO("Fase de lógica")
    }
}
