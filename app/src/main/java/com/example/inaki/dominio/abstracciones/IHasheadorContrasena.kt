package com.example.inaki.dominio.abstracciones

interface IHasheadorContrasena {
    fun hashear(clave: String): String
    fun verificar(clave: String, hash: String): Boolean
}
