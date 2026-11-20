package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.filtros.FiltroBusqueda
import com.example.inaki.dominio.modelo.Anuncio

class ServicioBusqueda(
    private val repositorio: IRepositorio<Anuncio>
) {
    fun buscar(filtros: List<FiltroBusqueda>): List<Anuncio> {
        TODO("Fase de lógica")
    }
}
