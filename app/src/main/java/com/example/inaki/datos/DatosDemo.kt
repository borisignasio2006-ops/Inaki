package com.example.inaki.datos

import com.example.inaki.dominio.enums.Moneda
import com.example.inaki.dominio.enums.TipoAnunciante
import com.example.inaki.dominio.enums.TipoInmueble
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
        val fechaAntigua = Date(ahora.time - 50L * 24 * 60 * 60 * 1000) // ~50 days ago

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
                plan = planes[0] // Gratis
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
                plan = planes[2] // Profesional
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

        val todosAnuncios = mutableListOf<Anuncio>()
        var idAnuncioSeq = 100

        // Helper to create anuncio
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
                "Borrador" -> {} // stays Borrador
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

        // 12 Anuncios
        // 1. Alquiler Departamento Sopocachi (Publicado)
        crearAnuncioItem(
            titulo = "Hermoso Departamento en Sopocachi",
            desc = "Amplio departamento de 3 dormitorios, iluminado, cerca de teleférico amarillo.",
            inmueble = Inmueble(201, TipoInmueble.DEPARTAMENTO, 120.0, 3, 2, true, true, "Gas domiciliario, expensas incluidas", Ubicacion(-16.5050, -68.1250, "Av. Arce #2345", false, zonas[2])).apply {
                fotos.add(Foto(1, "demo://foto/1", 1, true))
                fotos.add(Foto(2, "demo://foto/2", 2, false))
            },
            condicion = Alquiler(Moneda.BOB, "Pago adelantado", 3500.0, 3500.0, 12),
            propietario = ana,
            estadoDeseado = "Publicado"
        )

        // 2. Alquiler Habitacion Miraflores (Publicado)
        crearAnuncioItem(
            titulo = "Habitación amoblada en Miraflores",
            desc = "Ideal estudiantes o profesionales, baño compartido, incluye internet y agua.",
            inmueble = Inmueble(202, TipoInmueble.HABITACION, 25.0, 1, 1, true, false, "Internet, agua, luz", Ubicacion(-16.5000, -68.1100, "Calle Honduras #450", false, zonas[5])).apply {
                fotos.add(Foto(3, "demo://foto/3", 1, true))
            },
            condicion = Alquiler(Moneda.BOB, "Mes de garantía", 1200.0, 1200.0, 6),
            propietario = ana,
            estadoDeseado = "Publicado"
        )

        // 3. Venta Casa Calacoto (Publicado)
        crearAnuncioItem(
            titulo = "Moderna Casa en Calacoto Sur",
            desc = "Residencia con 5 dormitorios, jardín amplio, parrillero y garaje para 4 vehículos.",
            inmueble = Inmueble(203, TipoInmueble.CASA, 450.0, 5, 5, false, true, "Todos los servicios", Ubicacion(-16.5400, -68.0800, "Calle 15 de Calacoto", false, zonas[1])).apply {
                fotos.add(Foto(4, "demo://foto/4", 1, true))
                fotos.add(Foto(5, "demo://foto/5", 2, false))
                fotos.add(Foto(6, "demo://foto/6", 3, false))
            },
            condicion = Venta(Moneda.USD, "Papeles al día", 280000.0, true),
            propietario = carlos,
            estadoDeseado = "Publicado"
        )

        // 4. Venta Terreno Achumani (Publicado - Ubicación aproximada)
        crearAnuncioItem(
            titulo = "Lote de Terreno en Achumani",
            desc = "Excelente terreno plano, ideal para condominio o casa familiar, zona residencial.",
            inmueble = Inmueble(204, TipoInmueble.TERRENO, 600.0, 0, 0, false, true, "Disponibilidad de acometidas", Ubicacion(-16.5250, -68.0750, "Urbanización Los Pinos", true, zonas[4])).apply {
                fotos.add(Foto(7, "demo://foto/7", 1, true))
            },
            condicion = Venta(Moneda.USD, "Escritura pública", 120000.0, false),
            propietario = carlos,
            estadoDeseado = "Publicado"
        )

        // 5. Anticrético Departamento San Miguel (Publicado)
        crearAnuncioItem(
            titulo = "Amplio Departamento en San Miguel",
            desc = "Zona comercial y bancaria, 2 suites, living comedor con vista panorámica.",
            inmueble = Inmueble(205, TipoInmueble.DEPARTAMENTO, 140.0, 2, 3, false, false, "Expensas bajas", Ubicacion(-16.5350, -68.0850, "Montenegro #890", false, zonas[3])).apply {
                fotos.add(Foto(8, "demo://foto/8", 1, true))
                fotos.add(Foto(9, "demo://foto/9", 2, false))
            },
            condicion = Anticretico(Moneda.USD, "Folio real al día", 55000.0, 24),
            propietario = carlos,
            estadoDeseado = "Publicado"
        )

        // 6. Anticrético Oficina Obrajes (Publicado)
        crearAnuncioItem(
            titulo = "Oficina Ejecutiva en Obrajes",
            desc = "Excelente ubicación sobre avenida principal, 3 ambientes, recepción y baño privado.",
            inmueble = Inmueble(206, TipoInmueble.OFICINA, 80.0, 0, 1, false, false, "Seguridad 24 horas", Ubicacion(-16.5200, -68.1000, "Av. Costanera #1200", false, zonas[0])).apply {
                fotos.add(Foto(10, "demo://foto/10", 1, true))
            },
            condicion = Anticretico(Moneda.USD, "Factura disponible", 35000.0, 36),
            propietario = carlos,
            estadoDeseado = "Publicado"
        )

        // 7. Alquiler Local Comercial Miraflores (Publicado)
        crearAnuncioItem(
            titulo = "Local Comercial Céntrico Miraflores",
            desc = "Gran flujo peatonal, ideal farmacia, minimarket o sucursal bancaria.",
            inmueble = Inmueble(207, TipoInmueble.LOCAL_COMERCIAL, 90.0, 0, 1, false, false, "Medidor independiente", Ubicacion(-16.4950, -68.1150, "Plaza Villarroel", false, zonas[5])).apply {
                fotos.add(Foto(11, "demo://foto/11", 1, true))
            },
            condicion = Alquiler(Moneda.BOB, "2 meses de garantía", 6000.0, 12000.0, 24),
            propietario = carlos,
            estadoDeseado = "Publicado"
        )

        // 8. Venta Departamento Sopocachi (Publicado - Sin confirmar hace > 45 días)
        crearAnuncioItem(
            titulo = "Departamento Antiguo en Sopocachi Alto",
            desc = "Oportunidad de inversión, para refaccionar, muy céntrico.",
            inmueble = Inmueble(208, TipoInmueble.DEPARTAMENTO, 95.0, 2, 1, false, true, "Servicios básicos", Ubicacion(-16.5100, -68.1300, "Calle Belisario Salinas", false, zonas[2])).apply {
                fotos.add(Foto(12, "demo://foto/12", 1, true))
            },
            condicion = Venta(Moneda.USD, "Trato directo", 75000.0, true),
            propietario = carlos,
            estadoDeseado = "Publicado",
            fechaPub = fechaAntigua,
            fechaUltConf = fechaAntigua // > 30 days unconfirmed badge trigger
        )

        // 9. Anticrético Casa Achumani (Publicado)
        crearAnuncioItem(
            titulo = "Casa Cómoda en Achumani Bajo",
            desc = "3 dormitorios, garaje, pequeño jardín posterior, zona muy tranquila.",
            inmueble = Inmueble(209, TipoInmueble.CASA, 200.0, 3, 2, false, true, "Gas domiciliario", Ubicacion(-16.5180, -68.0720, "Calle 2 de Achumani", false, zonas[4])).apply {
                fotos.add(Foto(13, "demo://foto/13", 1, true))
            },
            condicion = Anticretico(Moneda.USD, "Garantía notariada", 45000.0, 24),
            propietario = carlos,
            estadoDeseado = "Publicado"
        )

        // 10. Borrador (Anuncio en borrador)
        crearAnuncioItem(
            titulo = "Borrador: Oficina en Calacoto",
            desc = "En proceso de redacción y carga de fotos.",
            inmueble = Inmueble(210, TipoInmueble.OFICINA, 50.0, 0, 1, false, false, "", Ubicacion(-16.5300, -68.0850, "Calle 21", false, zonas[1])).apply {
                fotos.add(Foto(14, "demo://foto/14", 1, true))
            },
            condicion = Alquiler(Moneda.USD, "", 800.0, 800.0, 12),
            propietario = carlos,
            estadoDeseado = "Borrador"
        )

        // 11. Pausado (Anuncio pausado)
        crearAnuncioItem(
            titulo = "Pausado: Depto Obrajes",
            desc = "Temporalmente no disponible para visitas.",
            inmueble = Inmueble(211, TipoInmueble.DEPARTAMENTO, 100.0, 2, 2, true, false, "", Ubicacion(-16.5220, -68.0980, "Av. Hernando Siles", false, zonas[0])).apply {
                fotos.add(Foto(15, "demo://foto/15", 1, true))
            },
            condicion = Alquiler(Moneda.BOB, "", 3000.0, 3000.0, 12),
            propietario = carlos,
            estadoDeseado = "Pausado"
        )

        // 12. Cerrado (Anuncio cerrado / alquilado)
        crearAnuncioItem(
            titulo = "Cerrado: Local Comercial San Miguel",
            desc = "Alquilado con éxito.",
            inmueble = Inmueble(212, TipoInmueble.LOCAL_COMERCIAL, 70.0, 0, 1, false, false, "", Ubicacion(-16.5360, -68.0860, "Blanco Galindo", false, zonas[3])).apply {
                fotos.add(Foto(16, "demo://foto/16", 1, true))
            },
            condicion = Alquiler(Moneda.USD, "", 1500.0, 3000.0, 12),
            propietario = carlos,
            estadoDeseado = "Cerrado"
        )

        return Pair(listOf(ana, carlos, admin), todosAnuncios)
    }
}
