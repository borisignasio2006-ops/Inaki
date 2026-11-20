package com.example.inaki.dominio.modelo

import com.example.inaki.dominio.enums.EstadoReporte
import com.example.inaki.dominio.enums.MotivoReporte
import java.util.Date

class Reporte(
    var id: Int,
    var motivo: MotivoReporte,
    var descripcion: String,
    var estado: EstadoReporte,
    var fecha: Date,
    var anuncio: Anuncio
)
