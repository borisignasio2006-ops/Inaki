package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.IPasarelaPago
import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.modelo.PlanPublicacion
import com.example.inaki.dominio.modelo.RolAnunciante

class ServicioSuscripcion(
    private val repositorio: IRepositorio<PlanPublicacion>,
    private val pasarela: IPasarelaPago
) {
    fun contratarPlan(anunciante: RolAnunciante, plan: PlanPublicacion) {
        TODO("Fase de lógica")
    }
}
