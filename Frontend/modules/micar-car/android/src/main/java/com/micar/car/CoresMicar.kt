package com.micar.car

import androidx.car.app.model.CarColor

internal object CoresMicar {
  val PRIMARIA = 0xFFFF7A0F.toInt()
  val NEUTRA = 0xFF5F6B73.toInt()

  val primaria: CarColor = CarColor.createCustom(PRIMARIA, PRIMARIA)
  val neutra: CarColor = CarColor.createCustom(NEUTRA, NEUTRA)
}
