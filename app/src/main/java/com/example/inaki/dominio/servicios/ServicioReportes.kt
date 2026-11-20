package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.enums.MotivoReporte
import com.example.inaki.dominio.modelo.Anuncio
import com.example.inaki.dominio.modelo.Reporte
import com.example.inaki.dominio.modelo.Usuario

class ServicioReportes(
    private val repositorio: IRepositorio<Reporte>
) {
    fun denunciar(usuario: Usuario, anuncio: Anuncio, motivo: MotivoReporte): Reporte {
        TODO("Fase de lógica")
    }

    fun resolver(reporte: Reporte, decision: Boolean) {
        TODO("Fase de lógica")
    }
}
