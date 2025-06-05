package com.example.unifor_gym.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.NotificacaoAdapter
import com.example.unifor_gym.models.Notificacao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class Notificacoes : Fragment() {

    private lateinit var recyclerNotificacoes: RecyclerView
    private lateinit var notificacaoAdapter: NotificacaoAdapter
    private var listaNotificacoes: MutableList<Notificacao> = mutableListOf()

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificacoes, container, false)

        recyclerNotificacoes = view.findViewById(R.id.recyclerNotificacoes)

        configurarRecyclerView()
        carregarNotificacoes()

        return view
    }

    private fun configurarRecyclerView() {
        notificacaoAdapter = NotificacaoAdapter(listaNotificacoes)
        recyclerNotificacoes.layoutManager = LinearLayoutManager(requireContext())
        recyclerNotificacoes.adapter = notificacaoAdapter
    }

    private fun carregarNotificacoes() {
        if (currentUserId.isEmpty()) {
            Log.w("Notificacoes", "Usuário não autenticado")
            Toast.makeText(requireContext(), "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val todasNotificacoesCombinadas = mutableListOf<Notificacao>()

        // Buscar notificações específicas do usuário
        db.collection("notificacoes")
            .whereEqualTo("userId", currentUserId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Notificacoes", "Erro ao carregar notificações do usuário", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val notificacoesUsuario = mutableListOf<Notificacao>()

                    for (doc in snapshots.documents) {
                        try {
                            val notificacao = doc.toObject(Notificacao::class.java)
                            if (notificacao != null) {
                                val notificacaoComTempo = notificacao.copy(
                                    tempo = formatarTempo(notificacao.timestamp)
                                )
                                notificacoesUsuario.add(notificacaoComTempo)
                            }
                        } catch (ex: Exception) {
                            Log.e("Notificacoes", "Erro ao converter documento: ${doc.id}", ex)
                        }
                    }

                    // Combinar com notificações globais
                    combinarNotificacoes(notificacoesUsuario, todasNotificacoesCombinadas)
                }
            }

        // Buscar notificações globais separadamente
        db.collection("notificacoes")
            .whereEqualTo("global", true)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("Notificacoes", "Erro ao carregar notificações globais", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    val notificacoesGlobais = mutableListOf<Notificacao>()

                    for (doc in snapshots.documents) {
                        try {
                            val notificacao = doc.toObject(Notificacao::class.java)
                            if (notificacao != null) {
                                val notificacaoComTempo = notificacao.copy(
                                    tempo = formatarTempo(notificacao.timestamp)
                                )
                                notificacoesGlobais.add(notificacaoComTempo)
                            }
                        } catch (ex: Exception) {
                            Log.e("Notificacoes", "Erro ao converter documento global: ${doc.id}", ex)
                        }
                    }

                    // Combinar com notificações do usuário
                    combinarNotificacoes(notificacoesGlobais, todasNotificacoesCombinadas)
                }
            }
    }

    private fun combinarNotificacoes(
        novasNotificacoes: List<Notificacao>,
        listaCombinada: MutableList<Notificacao>
    ) {
        // Remove notificações antigas da mesma fonte
        listaCombinada.removeAll { existente ->
            novasNotificacoes.any { nova -> nova.id == existente.id }
        }

        // Adiciona novas notificações
        listaCombinada.addAll(novasNotificacoes)

        // Ordena por timestamp (mais recentes primeiro)
        val notificacoesOrdenadas = listaCombinada
            .distinctBy { it.id }
            .sortedByDescending { it.timestamp }

        // Atualiza a lista e o adapter
        listaNotificacoes.clear()
        listaNotificacoes.addAll(notificacoesOrdenadas)
        notificacaoAdapter.atualizarLista(listaNotificacoes)

        Log.d("Notificacoes", "Total de notificações carregadas: ${notificacoesOrdenadas.size}")
    }

    private fun formatarTempo(timestamp: Date?): String {
        if (timestamp == null) return "Agora"

        val now = Date()
        val diffMs = now.time - timestamp.time
        val diffMinutes = diffMs / (1000 * 60)
        val diffHours = diffMs / (1000 * 60 * 60)
        val diffDays = diffMs / (1000 * 60 * 60 * 24)

        return when {
            diffMinutes < 1 -> "Agora"
            diffMinutes < 60 -> "Há ${diffMinutes}m"
            diffHours < 24 -> "Há ${diffHours}h"
            diffDays < 7 -> "Há ${diffDays}d"
            else -> {
                val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
                sdf.format(timestamp)
            }
        }
    }

    // Método público para criar notificações (pode ser chamado de outras partes do app)
    companion object {
        fun criarNotificacao(
            titulo: String,
            descricao: String,
            tipoIcone: String = "notificacoes",
            userId: String = "",
            global: Boolean = false
        ) {
            val db = FirebaseFirestore.getInstance()

            val notificacao = hashMapOf(
                "titulo" to titulo,
                "descricao" to descricao,
                "tipoIcone" to tipoIcone,
                "userId" to userId,
                "global" to global,
                "lida" to false,
                "timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()
            )

            db.collection("notificacoes")
                .add(notificacao)
                .addOnSuccessListener { documentReference ->
                    Log.d("Notificacoes", "Notificação criada: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Notificacoes", "Erro ao criar notificação", e)
                }
        }
    }
}