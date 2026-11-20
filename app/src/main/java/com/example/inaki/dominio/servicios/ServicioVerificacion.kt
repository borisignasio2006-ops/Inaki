package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.IAlmacenamientoArchivos
import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.modelo.Usuario
import com.example.inaki.dominio.modelo.Verificacion

class ServicioVerificacion(
    private val repositorio: IRepositorio<Verificacion>,
    private val almacenamiento: IAlmacenamientoArchivos
) {
    fun solicitar(anunciante: Usuario, evidencia: String): Verificacion {
        TODO("Fase de lógica")
    }

    fun revisar(verificacion: Verificacion, decision: Boolean) {
        TODO("Fase de lógica")
    }
}
