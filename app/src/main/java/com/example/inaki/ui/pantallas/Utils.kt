package com.example.inaki.ui.pantallas

import com.example.inaki.dominio.enums.Moneda
import java.text.NumberFormat
import java.util.Locale

fun formatPrecio(moneda: Moneda, valor: Double): String {
    val simbolo = if (moneda == Moneda.BOB) "Bs" else "\$us"
    val formatter = NumberFormat.getInstance(Locale("es", "BO")).apply {
        maximumFractionDigits = 0
    }
    return "$simbolo ${formatter.format(valor)}"
}
