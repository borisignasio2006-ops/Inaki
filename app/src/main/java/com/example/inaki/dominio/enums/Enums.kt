package com.example.inaki.dominio.enums

enum class TipoAnunciante { PROPIETARIO, CORREDOR, INMOBILIARIA }
enum class TipoInmueble { CASA, DEPARTAMENTO, HABITACION, OFICINA, LOCAL_COMERCIAL, TERRENO }
enum class Moneda { BOB, USD }
enum class TipoOperacion { ALQUILER, VENTA, ANTICRETICO }
enum class CanalContacto { APP, WHATSAPP, EMAIL, TELEFONO }
enum class EstadoConsulta { PENDIENTE, RESPONDIDA, CERRADA }
enum class EstadoVisita { SOLICITADA, CONFIRMADA, CANCELADA, REALIZADA }
enum class TipoVerificacion { IDENTIDAD, PROPIEDAD, UBICACION }
enum class EstadoVerificacion { PENDIENTE, APROBADA, RECHAZADA }
enum class MotivoReporte { FRAUDE, INFORMACION_FALSA, DUPLICADO, CONTENIDO_INAPROPIADO, YA_NO_DISPONIBLE, OTRO }
enum class EstadoReporte { ABIERTO, EN_REVISION, RESUELTO, DESCARTADO }
