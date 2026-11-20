package com.example.inaki.dominio.abstracciones

import android.util.Log

interface INotificador {
    fun enviar(destinatario: String, mensaje: String)
}

class NotificadorEmail : INotificador {
    override fun enviar(destinatario: String, mensaje: String) {
        Log.d("NotificadorEmail", "Enviando a $destinatario: $mensaje")
    }
}

class NotificadorWhatsApp : INotificador {
    override fun enviar(destinatario: String, mensaje: String) {
        Log.d("NotificadorWhatsApp", "Enviando a $destinatario: $mensaje")
    }
}

class NotificadorPush : INotificador {
    override fun enviar(destinatario: String, mensaje: String) {
        Log.d("NotificadorPush", "Enviando a $destinatario: $mensaje")
    }
}
