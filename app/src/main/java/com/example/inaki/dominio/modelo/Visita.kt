package com.example.inaki.dominio.modelo

import com.example.inaki.dominio.enums.EstadoVisita
import java.util.Date

class Visita(
    var id: Int,
    var fechaHora: Date,
    var estado: EstadoVisita
) {
    fun confirmar() {
        estado = EstadoVisita.CONFIRMADA
    }

    fun cancelar() {
        estado = EstadoVisita.CANCELADA
    }
}
