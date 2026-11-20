package com.example.inaki.dominio.abstracciones

interface IRepositorio<T> {
    fun buscarPorId(id: Int): T?
    fun guardar(entidad: T)
    fun eliminar(id: Int)
}
