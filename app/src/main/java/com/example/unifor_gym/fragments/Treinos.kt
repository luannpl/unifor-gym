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
import com.example.unifor_gym.models.Exercicio
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
        recyclerView.adapter = TreinoAdapterUser(treinos)

        return view
    }

    private fun criarTreinosExemplo(): List<Treino> {
        val exerciciosPeito = listOf(
            Exercicio("Supino Reto", "20kg", "12x"),
            Exercicio("Supino Inclinado", "25kg", "12x"),
            Exercicio("Voador", "45kg", "12x"),
            Exercicio("Crucifixo", "30kg", "12x"),
            Exercicio("Flexão", "-------", "40x")
        )
        val treinoPeito = Treino("PEITO", exerciciosPeito)

        val exerciciosCostas = listOf(
            Exercicio("Puxada Frontal", "40kg", "12x"),
            Exercicio("Remada Baixa", "35kg", "12x"),
            Exercicio("Pullover", "20kg", "15x"),
            Exercicio("Remada Curvada", "30kg", "12x")
        )
        val treinoCostas = Treino("COSTAS", exerciciosCostas)

        return listOf(treinoPeito, treinoCostas)
    }
}
