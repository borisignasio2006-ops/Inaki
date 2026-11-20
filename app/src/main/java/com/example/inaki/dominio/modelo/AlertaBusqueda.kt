package com.example.inaki.dominio.modelo

import com.example.inaki.dominio.enums.TipoOperacion

class AlertaBusqueda(
    var id: Int,
    var tipoOperacion: TipoOperacion,
    var precioMin: Double,
    var precioMax: Double,
    var activa: Boolean
) {
    var zona: Zona? = null

    fun coincideCon(anuncio: Anuncio): Boolean {
        if (!activa) return false
        if (anuncio.condicion.tipo() != tipoOperacion) return false
        val precioRef = anuncio.condicion.obtenerPrecioReferencia()
        if (precioRef < precioMin || precioRef > precioMax) return false
        if (zona != null && zona?.id != anuncio.inmueble.ubicacion.zona.id) return false
        return true
    }
}
