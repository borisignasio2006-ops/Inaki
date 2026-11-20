package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.INotificador
import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.modelo.Anuncio
import com.example.inaki.dominio.modelo.Consulta
import com.example.inaki.dominio.modelo.Usuario

class ServicioConsultas(
    private val repositorio: IRepositorio<Consulta>,
    private val notificador: INotificador
) {
    fun enviar(usuario: Usuario, anuncio: Anuncio, mensaje: String): Consulta {
        TODO("Fase de lógica")
    }

    fun responder(consulta: Consulta, texto: String) {
        TODO("Fase de lógica")
    }
}
