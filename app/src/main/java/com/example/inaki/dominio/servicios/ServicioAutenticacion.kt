package com.example.inaki.dominio.servicios

import com.example.inaki.dominio.abstracciones.IHasheadorContrasena
import com.example.inaki.dominio.abstracciones.IRepositorio
import com.example.inaki.dominio.modelo.Usuario

class ServicioAutenticacion(
    private val repositorio: IRepositorio<Usuario>,
    private val hasheador: IHasheadorContrasena
) {
    fun registrar(datos: Map<String, String>): Usuario {
        TODO("Fase de lógica")
    }

    fun iniciarSesion(correo: String, clave: String): Boolean {
        TODO("Fase de lógica")
    }
}
