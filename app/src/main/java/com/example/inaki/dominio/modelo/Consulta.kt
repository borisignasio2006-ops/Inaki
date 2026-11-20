package com.example.inaki.dominio.modelo

import com.example.inaki.dominio.enums.CanalContacto
import com.example.inaki.dominio.enums.EstadoConsulta
import java.util.Date

class Consulta(
    var id: Int,
    var mensaje: String,
    var canal: CanalContacto,
    var estado: EstadoConsulta,
    var fecha: Date,
    var anuncio: Anuncio
) {
    val visitas: MutableList<Visita> = mutableListOf()
}
