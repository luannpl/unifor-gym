package com.example.unifor_gym.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Objetivo(
    val id: String = "",
    val nome: String = "",
    val valorAtual: Double = 0.0,
    val metaDesejada: Double = 0.0,
    val unidade: String = "",
    val progresso: Double = 0.0,
    val dataCriacao: Long = System.currentTimeMillis(),
    val userId: String = ""
) : Parcelable {
    // Construtor vazio necessário para o Firebase
    constructor() : this("", "", 0.0, 0.0, "", 0.0, 0L, "")

    // Calcula o progresso automaticamente
    fun calcularProgresso(): Double {
        return if (metaDesejada > 0) {
            (valorAtual / metaDesejada * 100).coerceAtMost(100.0)
        } else {
            0.0
        }
    }
}