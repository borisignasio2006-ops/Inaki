package com.example.inaki.datos

import com.example.inaki.dominio.enums.*
import com.example.inaki.dominio.modelo.*
import java.util.Date

object DatosDemo {

    fun generarZonas(): List<Zona> {
        return listOf(
            Zona(1, "Obrajes", "La Paz", ""),
            Zona(2, "Calacoto", "La Paz", ""),
            Zona(3, "Sopocachi", "La Paz", ""),
            Zona(4, "San Miguel", "La Paz", ""),
            Zona(5, "Achumani", "La Paz", ""),
            Zona(6, "Miraflores", "La Paz", "")
        )
    }

    fun generarPlanes(): List<PlanPublicacion> {
        return listOf(
            PlanPublicacion(1, "Gratis", 0.0, 1, false),
            PlanPublicacion(2, "Básico", 99.0, 5, false),
            PlanPublicacion(3, "Profesional", 249.0, 20, true)
        )
    }

    fun generarUsuarios(zonas: List<Zona>, planes: List<PlanPublicacion>): Pair<List<Usuario>, List<Anuncio>> {
        val ahora = Date()

        val ana = Usuario(
            id = 1,
            nombre = "Ana Pérez",
            correo = "ana@inaki.bo",
            telefono = "70123456",
            fechaRegistro = ahora,
            credencial = Credencial("demo#1234", ahora)
        ).apply {
            roles.add(RolBuscador())
            val rolAna = RolAnunciante(TipoAnunciante.PROPIETARIO, false).apply {
                plan = planes[0]
            }
            roles.add(rolAna)
        }

        val carlos = Usuario(
            id = 2,
            nombre = "Carlos Gómez",
            correo = "carlos@inaki.bo",
            telefono = "71234567",
            fechaRegistro = ahora,
            credencial = Credencial("demo#1234", ahora)
        ).apply {
            roles.add(RolBuscador())
            val rolCarlos = RolAnunciante(TipoAnunciante.CORREDOR, true).apply {
                plan = planes[2] // Profesional (destacado = true)
            }
            roles.add(rolCarlos)
        }

        val admin = Usuario(
            id = 3,
            nombre = "Administrador Inaki",
            correo = "admin@inaki.bo",
            telefono = "79999999",
            fechaRegistro = ahora,
            credencial = Credencial("demo#admin", ahora)
        ).apply {
            roles.add(RolAdministrador())
            roles.add(RolBuscador())
        }

        val pedro = Usuario(
            id = 4,
            nombre = "Pedro Morales",
            correo = "pedro@inaki.bo",
            telefono = "72345678",
            fechaRegistro = ahora,
            credencial = Credencial("demo#1234", ahora)
        ).apply {
            roles.add(RolBuscador())
        }

        val todosAnuncios = mutableListOf<Anuncio>()
        var idAnuncioSeq = 100

        fun crearAnuncioItem(
            titulo: String,
            desc: String,
            inmueble: Inmueble,
            condicion: CondicionOperacion,
            propietario: Usuario,
            estadoDeseado: String,
            fechaPub: Date = ahora,
            fechaUltConf: Date = ahora
        ): Anuncio {
            val anuncio = Anuncio(
                id = ++idAnuncioSeq,
                titulo = titulo,
                descripcion = desc,
                fechaPublicacion = fechaPub,
                fechaUltimaConfirmacion = fechaUltConf,
                inmueble = inmueble,
                condicion = condicion
            )
            when (estadoDeseado) {
                "Publicado" -> anuncio.publicar()
                "Borrador" -> {}
                "Pausado" -> {
                    anuncio.publicar()
                    anuncio.pausar()
                }
                "Cerrado" -> {
                    anuncio.publicar()
                    anuncio.cerrar()
                }
            }
            propietario.anuncios.add(anuncio)
            todosAnuncios.add(anuncio)
            return anuncio
        }

        // 1. Ana publica Depto Sopocachi
        val a1 = crearAnuncioItem(
            titulo = "Hermoso Departamento en Sopocachi",
            desc = "Amplio departamento de 3 dormitorios, iluminado, cerca de teleférico amarillo.",
            inmueble = Inmueble(201, TipoInmueble.DEPARTAMENTO, 120.0, 3, 2, true, true, "Gas domiciliario, expensas incluidas", Ubicacion(-16.5050, -68.1250, "Av. Arce #2345", false, zonas[2])).apply {
                fotos.add(Foto(1, "demo://foto/1", 1, true))
            },
            condicion = Alquiler(Moneda.BOB, "Pago adelantado", 3500.0, 3500.0, 12),
            propietario = ana,
            estadoDeseado = "Publicado"
        )

        // 2. Ana publica Habitacion Miraflores
        crearAnuncioItem(
            titulo = "Habitación amoblada en Miraflores",
            desc = "Ideal estudiantes o profesionales, baño compartido, incluye internet y agua.",
            inmueble = Inmueble(202, TipoInmueble.HABITACION, 25.0, 1, 1, true, false, "Internet, agua, luz", Ubicacion(-16.5000, -68.1100, "Calle Honduras #450", false, zonas[5])).apply {
                fotos.add(Foto(2, "demo://foto/2", 1, true))
            },
            condicion = Alquiler(Moneda.BOB, "Mes de garantía", 1200.0, 1200.0, 6),
            propietario = ana,
            estadoDeseado = "Publicado"
        )

        // 3. Carlos publica Casa Calacoto (Destacado por plan Profesional)
        val cCasa = crearAnuncioItem(
            titulo = "Moderna Casa en Calacoto Sur",
            desc = "Residencia con 5 dormitorios, jardín amplio, parrillero y garaje para 4 vehículos.",
            inmueble = Inmueble(203, TipoInmueble.CASA, 450.0, 5, 5, false, true, "Todos los servicios", Ubicacion(-16.5400, -68.0800, "Calle 15 de Calacoto", false, zonas[1])).apply {
                fotos.add(Foto(3, "demo://foto/3", 1, true))
            },
            condicion = Venta(Moneda.USD, "Papeles al día", 280000.0, true),
            propietario = carlos,
            estadoDeseado = "Publicado"
        )

        // 4. Carlos publica Terreno Achumani
        crearAnuncioItem(
            titulo = "Lote de Terreno en Achumani",
            desc = "Excelente terreno plano, ideal para condominio o casa familiar, zona residencial.",
            inmueble = Inmueble(204, TipoInmueble.TERRENO, 600.0, 0, 0, false, true, "Disponibilidad de acometidas", Ubicacion(-16.5250, -68.0750, "Urbanización Los Pinos", true, zonas[4])).apply {
                fotos.add(Foto(4, "demo://foto/4", 1, true))
            },
            condicion = Venta(Moneda.USD, "Escritura pública", 120000.0, false),
            propietario = carlos,
            estadoDeseado = "Publicado"
        )

        // --- DEMO VERIFICACIONES ---
        val verif1 = Verificacion(1, TipoVerificacion.IDENTIDAD, EstadoVerificacion.APROBADA, ahora, "demo://evidencia/1")
        carlos.verificaciones.add(verif1)

        val verif2 = Verificacion(2, TipoVerificacion.PROPIEDAD, EstadoVerificacion.PENDIENTE, ahora, "demo://evidencia/2")
        ana.verificaciones.add(verif2)
        a1.verificaciones.add(verif2)

        val verif3 = Verificacion(3, TipoVerificacion.UBICACION, EstadoVerificacion.RECHAZADA, ahora, "demo://evidencia/3")
        carlos.verificaciones.add(verif3)
        cCasa.verificaciones.add(verif3)

        // --- DEMO CONSULTAS, VISITAS, ALERTAS, REPORTES ---
        val consAna1 = Consulta(1, "Me interesa la casa, ¿sigue disponible?", CanalContacto.APP, EstadoConsulta.PENDIENTE, ahora, cCasa).apply {
            visitas.add(Visita(1, Date(ahora.time + 86400000L * 2), EstadoVisita.SOLICITADA))
        }
        val consAna2 = Consulta(2, "¿El precio es negociable al contado?", CanalContacto.WHATSAPP, EstadoConsulta.RESPONDIDA, Date(ahora.time - 86400000L), cCasa).apply {
            visitas.add(Visita(2, Date(ahora.time + 86400000L), EstadoVisita.CONFIRMADA))
        }
        val consAna3 = Consulta(3, "Muchas gracias por la atención.", CanalContacto.EMAIL, EstadoConsulta.CERRADA, Date(ahora.time - 172800000L), cCasa)
        ana.consultas.addAll(listOf(consAna1, consAna2, consAna3))

        val consPedro1 = Consulta(4, "Hola, ¿admite mascota pequeña?", CanalContacto.APP, EstadoConsulta.PENDIENTE, ahora, a1).apply {
            visitas.add(Visita(3, Date(ahora.time + 86400000L * 3), EstadoVisita.SOLICITADA))
        }
        val consPedro2 = Consulta(5, "Quisiera agendar visita este fin de semana", CanalContacto.TELEFONO, EstadoConsulta.RESPONDIDA, Date(ahora.time - 43200000L), a1)
        pedro.consultas.addAll(listOf(consPedro1, consPedro2))

        val alerta1 = AlertaBusqueda(1, TipoOperacion.ALQUILER, 2000.0, 5000.0, true).apply {
            zona = zonas[2]
        }
        val alerta2 = AlertaBusqueda(2, TipoOperacion.VENTA, 50000.0, 300000.0, true).apply {
            zona = null
        }
        ana.alertas.addAll(listOf(alerta1, alerta2))

        val reporteAna = Reporte(1, MotivoReporte.INFORMACION_FALSA, "El número de habitaciones no coincide con la descripción.", EstadoReporte.ABIERTO, ahora, cCasa)
        ana.reportes.add(reporteAna)

        return Pair(listOf(ana, carlos, admin, pedro), todosAnuncios)
    }
}
