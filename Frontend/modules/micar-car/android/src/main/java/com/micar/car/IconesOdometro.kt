package com.micar.car

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import androidx.car.app.model.CarIcon
import androidx.core.graphics.drawable.IconCompat

internal object IconesOdometro {
  enum class Acao { CANCELAR, CONFIRMAR }

  private const val TAMANHO_BOTAO = 256f
  private const val ESPESSURA_SIMBOLO = 22f

  private val botoesAjuste = mutableMapOf<Long, CarIcon>()
  private val botoesAcao = mutableMapOf<Pair<Acao, Int>, CarIcon>()

  fun botaoAjuste(passo: Long): CarIcon = botoesAjuste.getOrPut(passo) {
    criarIcone(desenharBotaoAjuste(rotuloPasso(passo)))
  }

  fun botaoAcao(acao: Acao, cor: Int): CarIcon = botoesAcao.getOrPut(acao to cor) {
    criarIcone(desenharBotaoAcao(acao, cor))
  }

  fun rotuloPasso(passo: Long) = if (passo > 0) "+$passo" else "−${-passo}"

  private fun desenharBotaoAjuste(rotulo: String): Bitmap {
    val (bitmap, canvas) = criarCirculo(CoresMicar.PRIMARIA)
    val centro = TAMANHO_BOTAO / 2

    val pincelTexto = criarPincel(Color.WHITE).apply {
      typeface = Typeface.DEFAULT_BOLD
      textAlign = Paint.Align.CENTER
    }
    ajustarTamanho(pincelTexto, rotulo, larguraMaxima = TAMANHO_BOTAO * 0.7f, tamanhoMaximo = 104f)

    val metricas = pincelTexto.fontMetrics
    canvas.drawText(rotulo, centro, centro - (metricas.ascent + metricas.descent) / 2, pincelTexto)

    return bitmap
  }

  private fun desenharBotaoAcao(acao: Acao, cor: Int): Bitmap {
    val (bitmap, canvas) = criarCirculo(cor)
    val pincelSimbolo = criarPincel(Color.WHITE).apply {
      style = Paint.Style.STROKE
      strokeWidth = ESPESSURA_SIMBOLO
      strokeCap = Paint.Cap.ROUND
      strokeJoin = Paint.Join.ROUND
    }

    val simbolo = Path()
    when (acao) {
      Acao.CANCELAR -> {
        simbolo.moveTo(TAMANHO_BOTAO * 0.35f, TAMANHO_BOTAO * 0.35f)
        simbolo.lineTo(TAMANHO_BOTAO * 0.65f, TAMANHO_BOTAO * 0.65f)
        simbolo.moveTo(TAMANHO_BOTAO * 0.65f, TAMANHO_BOTAO * 0.35f)
        simbolo.lineTo(TAMANHO_BOTAO * 0.35f, TAMANHO_BOTAO * 0.65f)
      }
      Acao.CONFIRMAR -> {
        simbolo.moveTo(TAMANHO_BOTAO * 0.30f, TAMANHO_BOTAO * 0.52f)
        simbolo.lineTo(TAMANHO_BOTAO * 0.44f, TAMANHO_BOTAO * 0.66f)
        simbolo.lineTo(TAMANHO_BOTAO * 0.70f, TAMANHO_BOTAO * 0.38f)
      }
    }
    canvas.drawPath(simbolo, pincelSimbolo)

    return bitmap
  }

  private fun criarCirculo(cor: Int): Pair<Bitmap, Canvas> {
    val bitmap = Bitmap.createBitmap(TAMANHO_BOTAO.toInt(), TAMANHO_BOTAO.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val centro = TAMANHO_BOTAO / 2
    canvas.drawCircle(centro, centro, centro, criarPincel(cor))
    return bitmap to canvas
  }

  private fun criarPincel(cor: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = cor }

  private fun ajustarTamanho(pincel: Paint, texto: String, larguraMaxima: Float, tamanhoMaximo: Float) {
    pincel.textSize = tamanhoMaximo
    val largura = pincel.measureText(texto)
    if (largura > larguraMaxima) {
      pincel.textSize = tamanhoMaximo * larguraMaxima / largura
    }
  }

  private fun criarIcone(bitmap: Bitmap) = CarIcon.Builder(IconCompat.createWithBitmap(bitmap)).build()
}
