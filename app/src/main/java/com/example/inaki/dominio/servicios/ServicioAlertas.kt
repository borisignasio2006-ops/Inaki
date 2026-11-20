package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.INotificador
import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.modelo.AlertaBusqueda
import com.example.inaki.dominio.modelo.Anuncio
import com.example.inaki.dominio.modelo.Usuario

class ServicioAlertas(
    private val repositorio: IRepositorio<AlertaBusqueda>,
    private val notificador: INotificador
) {
    fun crear(usuario: Usuario, criterios: Map<String, String>): AlertaBusqueda {
        TODO("Fase de lógica")
    }

    fun evaluarCoincidencias(anuncio: Anuncio) {
        TODO("Fase de lógica")
    }
}
