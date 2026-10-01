package com.example.nominaapp

const val SMMLV_2026 = 1_750_905.0
const val AUXILIO_TRANSPORTE_2026 = 249_095.0
const val HORAS_ORDINARIAS_MES = 210.0

const val PORCENTAJE_SALUD = 0.04
const val PORCENTAJE_PENSION = 0.04
const val PORCENTAJE_FONDO_SOLIDARIDAD = 0.01

enum class RangoSalarial {
    RANGO_1,
    RANGO_2,
    RANGO_3
}

data class ResultadoNomina(
    val salarioBasico: Double,
    val valorHora: Double,
    val pagoExtrasDiurnas: Double,
    val pagoExtrasNocturnas: Double,
    val totalHorasExtra: Double,
    val ibc: Double,
    val auxilioTransporte: Double,
    val totalDevengado: Double,
    val aporteSalud: Double,
    val aportePension: Double,
    val fondoSolidaridad: Double,
    val totalDeducciones: Double,
    val salarioNeto: Double,
    val rango: RangoSalarial
)

fun calcularNomina(
    salarioBasico: Double,
    horasDiurnas: Double,
    horasNocturnas: Double,
    esDominical: Boolean,
    transporteEmpresa: Boolean
): ResultadoNomina {

    val valorHora = salarioBasico / HORAS_ORDINARIAS_MES

    val factorDiurno: Double
    val factorNocturno: Double

    if (esDominical) {
        factorDiurno = 2.15
        factorNocturno = 2.65
    } else {
        factorDiurno = 1.25
        factorNocturno = 1.75
    }

    val pagoExtrasDiurnas =
        horasDiurnas * valorHora * factorDiurno

    val pagoExtrasNocturnas =
        horasNocturnas * valorHora * factorNocturno

    val totalHorasExtra =
        pagoExtrasDiurnas + pagoExtrasNocturnas

    val ibc =
        salarioBasico + totalHorasExtra

    val auxilioTransporte =
        if (
            salarioBasico <= SMMLV_2026 * 2 &&
            !transporteEmpresa
        ) {
            AUXILIO_TRANSPORTE_2026
        } else {
            0.0
        }

    val totalDevengado =
        ibc + auxilioTransporte

    val aporteSalud =
        ibc * PORCENTAJE_SALUD

    val aportePension =
        ibc * PORCENTAJE_PENSION

    val fondoSolidaridad =
        if (ibc >= SMMLV_2026 * 4) {
            ibc * PORCENTAJE_FONDO_SOLIDARIDAD
        } else {
            0.0
        }

    val totalDeducciones =
        aporteSalud +
                aportePension +
                fondoSolidaridad

    val salarioNeto =
        totalDevengado - totalDeducciones

    val rango =
        clasificarRango(salarioBasico)

    return ResultadoNomina(
        salarioBasico = salarioBasico,
        valorHora = valorHora,
        pagoExtrasDiurnas = pagoExtrasDiurnas,
        pagoExtrasNocturnas = pagoExtrasNocturnas,
        totalHorasExtra = totalHorasExtra,
        ibc = ibc,
        auxilioTransporte = auxilioTransporte,
        totalDevengado = totalDevengado,
        aporteSalud = aporteSalud,
        aportePension = aportePension,
        fondoSolidaridad = fondoSolidaridad,
        totalDeducciones = totalDeducciones,
        salarioNeto = salarioNeto,
        rango = rango
    )
}

fun clasificarRango(
    salarioBasico: Double
): RangoSalarial {

    return when {
        salarioBasico <= SMMLV_2026 * 2 -> {
            RangoSalarial.RANGO_1
        }

        salarioBasico < SMMLV_2026 * 4 -> {
            RangoSalarial.RANGO_2
        }

        else -> {
            RangoSalarial.RANGO_3
        }
    }
}