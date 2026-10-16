package com.micar.car

import androidx.annotation.DrawableRes
import androidx.car.app.CarContext
import androidx.car.app.model.Action
import androidx.car.app.model.CarColor
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridSection
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.SectionedItemTemplate
import androidx.car.app.model.Template
import androidx.car.app.versioning.CarAppApiLevels
import androidx.core.graphics.drawable.IconCompat

class AjusteOdometroScreen(
  carContext: CarContext,
  private val veiculo: Veiculo,
) : MicarScreen(carContext) {
  private val odometroInicial = veiculo.odometroAtual ?: 0
  private var odometro = odometroInicial
  private var salvando = false

  private val possuiAlteracao: Boolean
    get() = odometro != odometroInicial

  override fun onGetTemplate(): Template =
    if (carContext.carAppApiLevel >= CarAppApiLevels.LEVEL_8) {
      criarTemplateComSecoes()
    } else {
      criarTemplateGradeUnica()
    }

  private fun criarTemplateComSecoes(): Template {
    val header = Header.Builder()
      .setTitle(criarTitulo())
      .setStartHeaderAction(Action.BACK)
      .addEndHeaderAction(criarAcaoCabecalho(R.drawable.micar_car_ic_close, CarColor.DEFAULT) { finish() })
      .addEndHeaderAction(criarAcaoCabecalho(R.drawable.micar_car_ic_check, corConfirmarCabecalho()) { confirmar() })
      .build()

    return SectionedItemTemplate.Builder()
      .setHeader(header)
      .addSection(criarSecaoAjuste("Diminuir", PASSOS_DIMINUIR))
      .addSection(criarSecaoAjuste("Aumentar", PASSOS_AUMENTAR))
      .build()
  }

  private fun criarSecaoAjuste(titulo: String, passos: List<Long>) =
    GridSection.Builder()
      .setTitle(titulo)
      .setItemSize(GridSection.ITEM_SIZE_MEDIUM)
      .setItems(passos.map(::criarItemAjuste))
      .build()

  private fun criarTemplateGradeUnica(): Template {
    val itens = ItemList.Builder()
    PASSOS_DIMINUIR.forEach { itens.addItem(criarItemAjuste(it)) }
    itens.addItem(criarItemAcao(IconesOdometro.Acao.CANCELAR, CoresMicar.NEUTRA) { finish() })
    PASSOS_AUMENTAR.forEach { itens.addItem(criarItemAjuste(it)) }
    itens.addItem(criarItemAcao(IconesOdometro.Acao.CONFIRMAR, corConfirmarGrade()) { confirmar() })

    return GridTemplate.Builder()
      .setTitle(criarTitulo())
      .setHeaderAction(Action.BACK)
      .setSingleList(itens.build())
      .build()
  }

  private fun criarItemAjuste(passo: Long) =
    GridItem.Builder()
      .setTitle("${IconesOdometro.rotuloPasso(passo)} km")
      .setImage(IconesOdometro.botaoAjuste(passo), GridItem.IMAGE_TYPE_LARGE)
      .setOnClickListener { ajustar(passo) }
      .build()

  private fun criarItemAcao(acao: IconesOdometro.Acao, cor: Int, aoClicar: () -> Unit) =
    GridItem.Builder()
      .setTitle(if (acao == IconesOdometro.Acao.CANCELAR) "Cancelar" else "Salvar")
      .setImage(IconesOdometro.botaoAcao(acao, cor), GridItem.IMAGE_TYPE_LARGE)
      .setOnClickListener { aoClicar() }
      .build()

  private fun criarAcaoCabecalho(@DrawableRes icone: Int, cor: CarColor, aoClicar: () -> Unit) =
    Action.Builder()
      .setIcon(
        CarIcon.Builder(IconCompat.createWithResource(carContext, icone))
          .setTint(cor)
          .build(),
      )
      .setOnClickListener { aoClicar() }
      .build()

  private fun corConfirmarCabecalho() = if (possuiAlteracao) CoresMicar.primaria else CarColor.DEFAULT

  private fun corConfirmarGrade() = if (possuiAlteracao) CoresMicar.PRIMARIA else CoresMicar.NEUTRA

  private fun criarTitulo(): String {
    if (salvando) return "Salvando…"

    val diferenca = odometro - odometroInicial
    val sufixo = when {
      diferenca > 0 -> " (+${formatarNumero(diferenca)})"
      diferenca < 0 -> " (−${formatarNumero(-diferenca)})"
      else -> ""
    }
    return formatarKm(odometro) + sufixo
  }

  private fun ajustar(passo: Long) {
    if (salvando) return

    odometro = maxOf(0, odometro + passo)
    invalidate()
  }

  private fun confirmar() {
    if (salvando) return

    if (!possuiAlteracao) {
      exibirAviso("Nenhuma alteração no odômetro.")
      return
    }

    val odometroAnterior = veiculo.odometroAtual
    if (odometroAnterior != null && odometro <= odometroAnterior) {
      exibirAviso("Odômetro deve ser maior que ${formatarKm(odometroAnterior)}.")
      return
    }

    val api = criarApi() ?: return exibirAviso(MENSAGEM_SEM_SESSAO)

    salvando = true
    invalidate()

    val odometroInformado = odometro
    executar({
      api.createRegistroOdometro(veiculo.id, odometroInformado)
      api.getVeiculo(veiculo.id)
    }) { veiculoAtualizado ->
      setResult(veiculoAtualizado)
      finish()
    }
  }

  override fun tratarErro(erro: Throwable) {
    salvando = false
    exibirAviso(mensagemDoErro(erro))
    invalidate()
  }

  private companion object {
    val PASSOS_DIMINUIR = listOf(-100L, -10L, -1L)
    val PASSOS_AUMENTAR = listOf(1L, 10L, 100L)
  }
}
