package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.modelo.Anuncio

class ServicioModeracion(
    private val repositorio: IRepositorio<Anuncio>
) {
    fun moderar(anuncio: Anuncio, decision: Boolean) {
        TODO("Fase de lógica")
    }
}
