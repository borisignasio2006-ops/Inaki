package com.example.inaki.dominio.modelo

import java.util.Date

class Usuario(
    var id: Int,
    var nombre: String,
    var correo: String,
    var telefono: String,
    var fechaRegistro: Date,
    var credencial: Credencial
) {
    val roles: MutableList<Rol> = mutableListOf()
    val anuncios: MutableList<Anuncio> = mutableListOf()
    val favoritos: MutableList<Favorito> = mutableListOf()
    val alertas: MutableList<AlertaBusqueda> = mutableListOf()
    val consultas: MutableList<Consulta> = mutableListOf()
    val verificaciones: MutableList<Verificacion> = mutableListOf()
    val reportes: MutableList<Reporte> = mutableListOf()
}
