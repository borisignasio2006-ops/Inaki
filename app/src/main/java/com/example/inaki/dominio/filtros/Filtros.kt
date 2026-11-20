package com.example.inaki.dominio.filtros

import com.example.inaki.dominio.enums.TipoInmueble
import com.example.inaki.dominio.modelo.Anuncio

interface FiltroBusqueda {
    fun cumple(anuncio: Anuncio): Boolean
}

class FiltroZona(private val zonaId: Int) : FiltroBusqueda {
    override fun cumple(anuncio: Anuncio): Boolean {
        return anuncio.inmueble.ubicacion.zona.id == zonaId
    }
}

class FiltroPrecio(private val min: Double, private val max: Double) : FiltroBusqueda {
    override fun cumple(anuncio: Anuncio): Boolean {
        val precio = anuncio.condicion.obtenerPrecioReferencia()
        return precio in min..max
    }
}

class FiltroTipoInmueble(private val tipo: TipoInmueble) : FiltroBusqueda {
    override fun cumple(anuncio: Anuncio): Boolean {
        return anuncio.inmueble.tipo == tipo
    }
}

class FiltroDormitorios(private val minimo: Int) : FiltroBusqueda {
    override fun cumple(anuncio: Anuncio): Boolean {
        return anuncio.inmueble.dormitorios >= minimo
    }
}
