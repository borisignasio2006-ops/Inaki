package com.example.inaki.ui.navegacion

sealed interface Pantalla {
    object Login : Pantalla
    object Registro : Pantalla
    object Main : Pantalla
    data class DetalleAnuncio(val anuncioId: Int) : Pantalla
}
