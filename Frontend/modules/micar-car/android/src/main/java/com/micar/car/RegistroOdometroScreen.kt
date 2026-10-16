package com.micar.car

import androidx.car.app.CarContext
import androidx.car.app.model.Action
import androidx.car.app.model.ActionStrip
import androidx.car.app.model.ListTemplate
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template

class RegistroOdometroScreen(carContext: CarContext) : MicarScreen(carContext) {
  private sealed interface Estado {
    data object Carregando : Estado

    data class Mensagem(val texto: String) : Estado

    data class Pronto(val veiculo: Veiculo) : Estado
  }

  private sealed interface Resolucao {
    data class Encontrado(val veiculo: Veiculo) : Resolucao

    data class Selecionar(val veiculos: List<Veiculo>) : Resolucao

    data object SemVeiculos : Resolucao
  }

  private var estado: Estado = Estado.Carregando

  init {
    carregar()
  }

  override fun onGetTemplate(): Template = when (val atual = estado) {
    Estado.Carregando -> ListTemplate.Builder()
      .setTitle(TITULO)
      .setHeaderAction(Action.APP_ICON)
      .setLoading(true)
      .build()

    is Estado.Mensagem -> MessageTemplate.Builder(atual.texto)
      .setTitle(TITULO)
      .setHeaderAction(Action.APP_ICON)
      .addAction(
        Action.Builder()
          .setTitle("Tentar novamente")
          .setOnClickListener { carregar() }
          .build(),
      )
      .build()

    is Estado.Pronto -> criarTemplateVisor(atual.veiculo)
  }

  private fun criarTemplateVisor(veiculo: Veiculo): Template {
    val odometro = veiculo.odometroAtual?.let(::formatarKm) ?: "Sem registro de odômetro"

    return MessageTemplate.Builder("$odometro\nPlaca ${veiculo.placa}")
      .setTitle(TITULO)
      .setHeaderAction(Action.APP_ICON)
      .setActionStrip(
        ActionStrip.Builder()
          .addAction(
            Action.Builder()
              .setTitle("Trocar veículo")
              .setOnClickListener { trocarVeiculo() }
              .build(),
          )
          .build(),
      )
      .addAction(
        Action.Builder()
          .setTitle("Atualizar odômetro")
          .setFlags(Action.FLAG_PRIMARY)
          .setBackgroundColor(CoresMicar.primaria)
          .setOnClickListener { abrirAjuste(veiculo) }
          .build(),
      )
      .build()
  }

  private fun abrirAjuste(veiculo: Veiculo) {
    screenManager.pushForResult(AjusteOdometroScreen(carContext, veiculo)) { resultado ->
      val veiculoAtualizado = resultado as? Veiculo ?: return@pushForResult

      veiculoAtualizado.odometroAtual?.let { exibirAviso("Registrado: ${formatarKm(it)}") }
      atualizar(Estado.Pronto(veiculoAtualizado))
    }
  }

  private fun carregar() {
    val api = criarApi() ?: return atualizar(Estado.Mensagem(MENSAGEM_SEM_SESSAO))

    atualizar(Estado.Carregando)
    executar({ resolverVeiculo(api) }) { resolucao ->
      when (resolucao) {
        is Resolucao.Encontrado -> atualizar(Estado.Pronto(resolucao.veiculo))
        is Resolucao.Selecionar -> selecionarVeiculo(resolucao.veiculos)
        Resolucao.SemVeiculos -> atualizar(Estado.Mensagem(MENSAGEM_SEM_VEICULOS))
      }
    }
  }

  private fun resolverVeiculo(api: MicarApi): Resolucao {
    val veiculoId = sessionStore.veiculoId

    if (veiculoId != null) {
      try {
        return Resolucao.Encontrado(api.getVeiculo(veiculoId))
      } catch (_: NotFoundException) {
        sessionStore.veiculoId = null
      }
    }

    val veiculos = api.getVeiculos()

    return when (veiculos.size) {
      0 -> Resolucao.SemVeiculos
      1 -> {
        sessionStore.veiculoId = veiculos.first().id
        Resolucao.Encontrado(veiculos.first())
      }
      else -> Resolucao.Selecionar(veiculos)
    }
  }

  private fun trocarVeiculo() {
    val api = criarApi() ?: return atualizar(Estado.Mensagem(MENSAGEM_SEM_SESSAO))
    executar({ api.getVeiculos() }) { veiculos -> selecionarVeiculo(veiculos) }
  }

  private fun selecionarVeiculo(veiculos: List<Veiculo>) {
    val tela = SelecionarVeiculoScreen(carContext, veiculos, sessionStore.veiculoId)

    screenManager.pushForResult(tela) { resultado ->
      val veiculo = resultado as? Veiculo

      if (veiculo != null) {
        sessionStore.veiculoId = veiculo.id
        atualizar(Estado.Pronto(veiculo))
      } else if (estado !is Estado.Pronto) {
        atualizar(Estado.Mensagem(MENSAGEM_NENHUM_SELECIONADO))
      }
    }
  }

  override fun tratarErro(erro: Throwable) {
    if (erro is UnauthorizedException || estado !is Estado.Pronto) {
      atualizar(Estado.Mensagem(mensagemDoErro(erro)))
    } else {
      exibirAviso(mensagemDoErro(erro))
    }
  }

  private fun atualizar(novoEstado: Estado) {
    estado = novoEstado
    invalidate()
  }

  private companion object {
    const val TITULO = "Registrar odômetro"
    const val MENSAGEM_SEM_VEICULOS = "Cadastre um veículo no app do celular."
    const val MENSAGEM_NENHUM_SELECIONADO = "Nenhum veículo selecionado."
  }
}
