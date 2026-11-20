package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.modelo.Consulta
import com.example.inaki.dominio.modelo.Visita
import java.util.Date

class ServicioVisitas(
    private val repositorio: IRepositorio<Visita>
) {
    fun solicitar(consulta: Consulta, fechaHora: Date): Visita {
        TODO("Fase de lógica")
    }
}
