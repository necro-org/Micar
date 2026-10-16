package com.micar.car

import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.io.IOException
import java.util.concurrent.Executors

abstract class MicarScreen(carContext: CarContext) : Screen(carContext) {
  protected val sessionStore = CarSessionStore(carContext)
  private val executor = Executors.newSingleThreadExecutor()
  private val mainExecutor = ContextCompat.getMainExecutor(carContext)

  init {
    lifecycle.addObserver(object : DefaultLifecycleObserver {
      override fun onDestroy(owner: LifecycleOwner) {
        executor.shutdownNow()
      }
    })
  }

  protected abstract fun tratarErro(erro: Throwable)

  protected fun criarApi(): MicarApi? {
    val token = sessionStore.token ?: return null
    val apiUrl = sessionStore.apiUrl ?: return null
    return MicarApi(apiUrl, token)
  }

  protected fun <T> executar(tarefa: () -> T, aoConcluir: (T) -> Unit) {
    executor.execute {
      val resultado = runCatching(tarefa)

      mainExecutor.execute {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) return@execute
        resultado.fold(aoConcluir, ::tratarErro)
      }
    }
  }

  protected fun mensagemDoErro(erro: Throwable) = when (erro) {
    is UnauthorizedException -> MENSAGEM_SEM_SESSAO
    is IOException -> MENSAGEM_SEM_CONEXAO
    else -> erro.message ?: MENSAGEM_ERRO_GENERICO
  }

  protected fun exibirAviso(mensagem: String) {
    CarToast.makeText(carContext, mensagem, CarToast.LENGTH_LONG).show()
  }

  protected companion object {
    const val MENSAGEM_SEM_SESSAO = "Abra o Micar no celular para entrar."
    const val MENSAGEM_SEM_CONEXAO = "Não foi possível conectar ao servidor."
    const val MENSAGEM_ERRO_GENERICO = "Não foi possível processar a sua requisição."
  }
}
