package com.micar.car

import java.text.NumberFormat
import java.util.Locale

private val FORMATO_NUMERO: NumberFormat = NumberFormat.getIntegerInstance(Locale.forLanguageTag("pt-BR"))

internal fun formatarNumero(valor: Long): String = FORMATO_NUMERO.format(valor)

internal fun formatarKm(km: Long) = "${formatarNumero(km)} km"
