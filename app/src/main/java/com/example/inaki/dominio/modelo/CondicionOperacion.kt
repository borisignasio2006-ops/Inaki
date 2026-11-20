package com.example.inaki.dominio.modelo

import com.example.inaki.dominio.enums.Moneda
import com.example.inaki.dominio.enums.TipoOperacion

abstract class CondicionOperacion(
    var moneda: Moneda,
    var condicionesExtra: String
) {
    abstract fun tipo(): TipoOperacion
    abstract fun obtenerPrecioReferencia(): Double
    abstract fun calcularCostoTotal(horizonteMeses: Int): Double
}

class Alquiler(
    moneda: Moneda,
    condicionesExtra: String,
    var precioMensual: Double,
    var garantia: Double,
    var mesesMinimos: Int
) : CondicionOperacion(moneda, condicionesExtra) {
    override fun tipo(): TipoOperacion = TipoOperacion.ALQUILER
    override fun obtenerPrecioReferencia(): Double = precioMensual
    override fun calcularCostoTotal(horizonteMeses: Int): Double = garantia + (precioMensual * horizonteMeses)
}

class Venta(
    moneda: Moneda,
    condicionesExtra: String,
    var precio: Double,
    var negociable: Boolean
) : CondicionOperacion(moneda, condicionesExtra) {
    override fun tipo(): TipoOperacion = TipoOperacion.VENTA
    override fun obtenerPrecioReferencia(): Double = precio
    override fun calcularCostoTotal(horizonteMeses: Int): Double = precio
}

class Anticretico(
    moneda: Moneda,
    condicionesExtra: String,
    var montoAnticretico: Double,
    var plazoMeses: Int
) : CondicionOperacion(moneda, condicionesExtra) {
    override fun tipo(): TipoOperacion = TipoOperacion.ANTICRETICO
    override fun obtenerPrecioReferencia(): Double = montoAnticretico
    override fun calcularCostoTotal(horizonteMeses: Int): Double = montoAnticretico
    fun calcularCapitalInmovilizado(): Double = montoAnticretico
}
