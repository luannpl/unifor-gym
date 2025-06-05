package com.example.unifor_gym.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Notificacao(
    @DocumentId
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val tempo: String = "",
    val lida: Boolean = false,
    val tipoIcone: String = "",
    val userId: String = "", // Para notificações específicas do usuário
    @ServerTimestamp
    val timestamp: Date? = null, // Para ordenação por data real
    val global: Boolean = false // Para notificações globais (todos os usuários)
) {
    // Construtor vazio necessário para o Firebase
    constructor() : this("", "", "", "", false, "", "", null, false)
}