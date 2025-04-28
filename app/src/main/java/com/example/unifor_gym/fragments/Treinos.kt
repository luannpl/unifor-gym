package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.TreinoAdapterUser
import com.example.unifor_gym.models.ExercicioTreino
import com.example.unifor_gym.models.Treino

class Treinos : Fragment() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_treinos, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewTreinos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val treinos = criarTreinosExemplo()

        // Criar adapter com callback de clique
        val adapter = TreinoAdapterUser(treinos) { treinoClicado ->
            // Quando um treino for clicado, navegar para TreinoDetalhes
            val bundle = Bundle().apply {
                putString("treino_nome", treinoClicado.titulo)
                putParcelableArrayList("exercicios", ArrayList(treinoClicado.exercicios))
            }

            // Criar uma instância do TreinoDetalhes e passar os argumentos
            val treinoDetalhesFragment = TreinoDetalhes().apply {
                arguments = bundle
            }

            // Fazer a transação de fragmentos
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, treinoDetalhesFragment) // Use o ID do seu container de fragmentos
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter

        return view
    }

    private fun criarTreinosExemplo(): List<Treino> {
        val exerciciosPeito = listOf(
            ExercicioTreino("Supino Reto", "20kg", "12x"),
            ExercicioTreino("Supino Inclinado", "25kg", "12x"),
            ExercicioTreino("Voador", "45kg", "12x"),
            ExercicioTreino("Crucifixo", "30kg", "12x"),
            ExercicioTreino("Flexão", "-------", "40x")
        )
        val treinoPeito = Treino("PEITO", exerciciosPeito)

        val exerciciosCostas = listOf(
            ExercicioTreino("Puxada Frontal", "40kg", "12x"),
            ExercicioTreino("Remada Baixa", "35kg", "12x"),
            ExercicioTreino("Pullover", "20kg", "15x"),
            ExercicioTreino("Remada Curvada", "30kg", "12x")
        )
        val treinoCostas = Treino("COSTAS", exerciciosCostas)

        return listOf(treinoPeito, treinoCostas)
    }
}