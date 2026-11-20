package com.example.inaki.dominio.abstracciones

import com.example.inaki.dominio.enums.Moneda

interface IPasarelaPago {
    fun cobrar(monto: Double, moneda: Moneda, medio: String): Boolean
}

class PasarelaQR : IPasarelaPago {
    override fun cobrar(monto: Double, moneda: Moneda, medio: String): Boolean {
        return monto >= 0
    }
}

class PasarelaTarjeta : IPasarelaPago {
    override fun cobrar(monto: Double, moneda: Moneda, medio: String): Boolean {
        return monto >= 0
    }
}
