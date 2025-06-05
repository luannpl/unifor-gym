package com.example.unifor_gym.utils

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

object NotificationHelper {

    private val db = FirebaseFirestore.getInstance()

    /**
     * Cria uma notificação para um usuário específico
     */
    fun criarNotificacaoUsuario(
        userId: String,
        titulo: String,
        descricao: String,
        tipoIcone: String = "notificacoes"
    ) {
        val notificacao = hashMapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "tipoIcone" to tipoIcone,
            "userId" to userId,
            "global" to false,
            "lida" to false,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("notificacoes")
            .add(notificacao)
            .addOnSuccessListener { documentReference ->
                Log.d("NotificationHelper", "Notificação criada para usuário $userId: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("NotificationHelper", "Erro ao criar notificação para usuário $userId", e)
            }
    }

    /**
     * Cria uma notificação global (para todos os usuários)
     */
    fun criarNotificacaoGlobal(
        titulo: String,
        descricao: String,
        tipoIcone: String = "notificacoes"
    ) {
        val notificacao = hashMapOf(
            "titulo" to titulo,
            "descricao" to descricao,
            "tipoIcone" to tipoIcone,
            "userId" to "", // Vazio para notificações globais
            "global" to true,
            "lida" to false,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("notificacoes")
            .add(notificacao)
            .addOnSuccessListener { documentReference ->
                Log.d("NotificationHelper", "Notificação global criada: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("NotificationHelper", "Erro ao criar notificação global", e)
            }
    }

    /**
     * Notificações de treino
     */
    fun notificarTreinoCriado(userId: String, nomeTreino: String) {
        criarNotificacaoUsuario(
            userId = userId,
            titulo = "Novo treino criado!",
            descricao = "Seu treino '$nomeTreino' foi criado com sucesso",
            tipoIcone = "exercicios"
        )
    }

    /**
     * Notificações de aula
     */
    fun notificarInscricaoAula(userId: String, nomeAula: String) {
        criarNotificacaoUsuario(
            userId = userId,
            titulo = "Inscrição confirmada",
            descricao = "Você foi inscrito na aula de $nomeAula",
            tipoIcone = "aulas"
        )
    }

    fun notificarCancelamentoAula(userId: String, nomeAula: String) {
        criarNotificacaoUsuario(
            userId = userId,
            titulo = "Inscrição cancelada",
            descricao = "Sua inscrição na aula de $nomeAula foi cancelada",
            tipoIcone = "aulas"
        )
    }

    /**
     * Notificações de equipamentos
     */
    fun notificarEquipamentoManutencao(nomeEquipamento: String) {
        criarNotificacaoGlobal(
            titulo = "Equipamento em manutenção",
            descricao = "$nomeEquipamento está temporariamente indisponível",
            tipoIcone = "aparelhos"
        )
    }

    fun notificarEquipamentoOperacional(nomeEquipamento: String) {
        criarNotificacaoGlobal(
            titulo = "Equipamento disponível",
            descricao = "$nomeEquipamento voltou a funcionar normalmente",
            tipoIcone = "aparelhos"
        )
    }

    /**
     * Notificações gerais do sistema
     */
    fun notificarNovaAulaDisponivel(nomeAula: String, instrutor: String) {
        criarNotificacaoGlobal(
            titulo = "Nova aula disponível!",
            descricao = "A aula de $nomeAula com $instrutor está aberta para inscrições",
            tipoIcone = "aulas"
        )
    }

    fun notificarManutencaoSistema(dataManutencao: String) {
        criarNotificacaoGlobal(
            titulo = "Manutenção programada",
            descricao = "O sistema estará em manutenção no dia $dataManutencao",
            tipoIcone = "notificacoes"
        )
    }
}