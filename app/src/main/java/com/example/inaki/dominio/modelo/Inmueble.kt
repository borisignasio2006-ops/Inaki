package com.example.inaki.dominio.modelo

import com.example.inaki.dominio.enums.TipoInmueble

class Inmueble(
    var id: Int,
    var tipo: TipoInmueble,
    var superficieM2: Double,
    var dormitorios: Int,
    var banos: Int,
    var amoblado: Boolean,
    var aceptaMascotas: Boolean,
    var serviciosIncluidos: String,
    var ubicacion: Ubicacion
) {
    val fotos: MutableList<Foto> = mutableListOf()
}
