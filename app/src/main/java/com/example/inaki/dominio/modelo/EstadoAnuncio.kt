package com.example.inaki.dominio.modelo

import java.util.Date

interface EstadoAnuncio {
    fun publicar(anuncio: Anuncio)
    fun pausar(anuncio: Anuncio)
    fun cerrar(anuncio: Anuncio)
}

class Borrador : EstadoAnuncio {
    override fun publicar(anuncio: Anuncio) {
        val ahora = Date()
        anuncio.fechaPublicacion = ahora
        anuncio.fechaUltimaConfirmacion = ahora
        anuncio.estado = Publicado()
    }

    override fun pausar(anuncio: Anuncio) {
        // Nada
    }

    override fun cerrar(anuncio: Anuncio) {
        anuncio.estado = Cerrado()
    }
}

class Publicado : EstadoAnuncio {
    override fun publicar(anuncio: Anuncio) {
        // Nada
    }

    override fun pausar(anuncio: Anuncio) {
        anuncio.estado = Pausado()
    }

    override fun cerrar(anuncio: Anuncio) {
        anuncio.estado = Cerrado()
    }
}

class Pausado : EstadoAnuncio {
    override fun publicar(anuncio: Anuncio) {
        anuncio.fechaUltimaConfirmacion = Date()
        anuncio.estado = Publicado()
    }

    override fun pausar(anuncio: Anuncio) {
        // Nada
    }

    override fun cerrar(anuncio: Anuncio) {
        anuncio.estado = Cerrado()
    }
}

class Cerrado : EstadoAnuncio {
    override fun publicar(anuncio: Anuncio) {
        // Nada
    }

    override fun pausar(anuncio: Anuncio) {
        // Nada
    }

    override fun cerrar(anuncio: Anuncio) {
        // Nada
    }
}
