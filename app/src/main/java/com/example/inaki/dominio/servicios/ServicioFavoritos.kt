package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.modelo.Anuncio
import com.example.inaki.dominio.modelo.Favorito
import com.example.inaki.dominio.modelo.Usuario

class ServicioFavoritos(
    private val repositorio: IRepositorio<Favorito>
) {
    fun guardar(usuario: Usuario, anuncio: Anuncio) {
        TODO("Fase de lógica")
    }

    fun quitar(usuario: Usuario, anuncio: Anuncio) {
        TODO("Fase de lógica")
    }
}
