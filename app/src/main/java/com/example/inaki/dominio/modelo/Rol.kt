package com.example.inaki.dominio.modelo

import com.example.inaki.dominio.enums.TipoAnunciante

interface Rol {
    fun tienePermiso(accion: String): Boolean
}

class RolBuscador : Rol {
    override fun tienePermiso(accion: String): Boolean {
        return when (accion) {
            "buscar", "guardar_favorito", "crear_alerta", "enviar_consulta", "solicitar_visita", "reportar" -> true
            else -> false
        }
    }
}

class RolAnunciante(
    var tipo: TipoAnunciante,
    var verificado: Boolean
) : Rol {
    var plan: PlanPublicacion? = null

    override fun tienePermiso(accion: String): Boolean {
        return when (accion) {
            "publicar_anuncio", "pausar_anuncio", "cerrar_anuncio", "responder_consulta", "gestionar_visitas", "solicitar_verificacion", "contratar_plan" -> true
            else -> false
        }
    }
}

class RolAdministrador : Rol {
    override fun tienePermiso(accion: String): Boolean {
        return when (accion) {
            "moderar_anuncio", "revisar_verificacion", "resolver_reporte" -> true
            else -> false
        }
    }
}
