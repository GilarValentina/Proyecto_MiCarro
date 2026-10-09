package com.example.carro.domain.model

/** Tipos de vehículo soportados (RF-01). */
enum class TipoVehiculo { AUTOMOVIL, CAMIONETA, MOTOCICLETA }

/** Estado de una actividad del plan de mantenimiento (RF-14, RN-01, RN-02). */
enum class EstadoActividad { AL_DIA, PROXIMA, VENCIDA, SIN_PROGRAMACION }

/** Naturaleza del mantenimiento registrado (RF-17). */
enum class TipoMantenimiento { PREVENTIVO, CORRECTIVO }

/** Tipos de documentos legales con vencimiento (RF-32). */
enum class TipoDocumento { SOAT, REVISION_TECNICO_MECANICA, SEGURO, OTRO }

/** Origen de una alerta local (RF-28, RF-33). */
enum class TipoAlerta { MANTENIMIENTO, DOCUMENTO }
