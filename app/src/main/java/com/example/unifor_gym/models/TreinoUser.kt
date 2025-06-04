package com.example.unifor_gym.models

import com.google.firebase.firestore.DocumentId

data class TreinoUser(
    @DocumentId
    val id: String = "",
    val usuarioId: String = "",
    val tipoTreino: String = "",
    val diasSemana: String = "",
    val exercicios: List<Exercicio> = emptyList(),
    val dataCriacao: Long = System.currentTimeMillis()
) {
    constructor() : this("", "", "", "", emptyList(), 0)
}

