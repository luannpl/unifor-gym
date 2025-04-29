package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.NotificacaoAdapter
import com.example.unifor_gym.models.Notificacao

class Notificacoes : Fragment() {

    private lateinit var recyclerNotificacoes: RecyclerView
    private lateinit var btnTodas: TextView
    private lateinit var btnNaoLidas: TextView
    private lateinit var btnTodasCard: CardView
    private lateinit var btnNaoLidasCard: CardView

    private lateinit var notificacaoAdapter: NotificacaoAdapter
    private lateinit var listaNotificacoes: List<Notificacao>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificacoes, container, false)

        recyclerNotificacoes = view.findViewById(R.id.recyclerNotificacoes)
        btnTodas = view.findViewById(R.id.btnTodas)
        btnNaoLidas = view.findViewById(R.id.btnNaoLidas)
        btnTodasCard = view.findViewById(R.id.btnTodasCard)
        btnNaoLidasCard = view.findViewById(R.id.btnNaoLidasCard)
        configurarRecyclerView()
        configurarBotoes()

        return view
    }

    private fun configurarRecyclerView() {
        listaNotificacoes = gerarMockNotificacoes()

        notificacaoAdapter = NotificacaoAdapter(listaNotificacoes)
        recyclerNotificacoes.layoutManager = LinearLayoutManager(requireContext())
        recyclerNotificacoes.adapter = notificacaoAdapter
    }

    private fun configurarBotoes() {
        // Definir seleção inicial
        selecionarBotao(true)

        btnTodas.setOnClickListener {
            selecionarBotao(true)
            notificacaoAdapter.atualizarLista(listaNotificacoes)
        }

        btnNaoLidas.setOnClickListener {
            selecionarBotao(false)
            val naoLidas = listaNotificacoes.filter { !it.lida }
            notificacaoAdapter.atualizarLista(naoLidas)
        }
    }

    private fun selecionarBotao(todasSelecionado: Boolean) {
        if (todasSelecionado) {
            btnTodas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
            btnTodas.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

            btnNaoLidas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            btnNaoLidas.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        } else {
            btnTodas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            btnTodas.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            btnNaoLidas.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
            btnNaoLidas.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    private fun gerarMockNotificacoes(): List<Notificacao> {
        return listOf(
            Notificacao("Treino de perna hoje", "Faça seu exercício diário", "Há 2 horas", false, "exercicios"),
            Notificacao("Treino de peito hoje", "Faça seu exercício diário", "Há 1 dia", true, "exercicios"),
            Notificacao("Treino de costa hoje", "Faça seu exercício diário", "Há 2 dias", true, "exercicios"),
            Notificacao("Conheça o crossfit", "Faça uma aula experimental", "Há 2 dias", false, "notificacoes"),
            Notificacao("Verifique sua conta", "Verificação de conta", "Há 3 dias", true, "notificacoes")
        )
    }
}