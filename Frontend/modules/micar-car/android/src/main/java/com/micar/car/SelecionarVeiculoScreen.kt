package com.micar.car

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.constraints.ConstraintManager
import androidx.car.app.model.Action
import androidx.car.app.model.ItemList
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.Row
import androidx.car.app.model.Template

class SelecionarVeiculoScreen(
  carContext: CarContext,
  private val veiculos: List<Veiculo>,
  private val veiculoSelecionadoId: String?,
) : Screen(carContext) {
  override fun onGetTemplate(): Template {
    val limite = carContext.getCarService(ConstraintManager::class.java)
      .getContentLimit(ConstraintManager.CONTENT_LIMIT_TYPE_LIST)

    val itens = ItemList.Builder()
    veiculos.take(limite).forEach { veiculo ->
      val descricao =
        if (veiculo.id == veiculoSelecionadoId) "${veiculo.placa} · selecionado" else veiculo.placa

      itens.addItem(
        Row.Builder()
          .setTitle(veiculo.apelido)
          .addText(descricao)
          .setOnClickListener {
            setResult(veiculo)
            finish()
          }
          .build(),
      )
    }

    return ListTemplate.Builder()
      .setTitle("Selecionar veículo")
      .setHeaderAction(Action.BACK)
      .setSingleList(itens.build())
      .build()
  }
}
