package com.example.inaki.dominio.modelo

import com.example.inaki.dominio.enums.EstadoVerificacion
import com.example.inaki.dominio.enums.TipoVerificacion
import java.util.Date

class Verificacion(
    var id: Int,
    var tipo: TipoVerificacion,
    var estado: EstadoVerificacion,
    var fecha: Date,
    var evidenciaUrl: String
)
