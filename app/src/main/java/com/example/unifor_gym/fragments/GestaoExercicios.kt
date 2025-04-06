package com.example.unifor_gym.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.ExercicioAdapter

data class Exercicio (
    val nome: String,
    val nivel: String,
    val grupoMuscular: String
)

class GestaoExercicios : Fragment() {
    private lateinit var recyclerExercicios: RecyclerView
    private lateinit var listaDeExercicios: List<Exercicio>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gestao_exercicios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerExercicios = view.findViewById(R.id.recyclerExercicios)

        listaDeExercicios = listOf(
            Exercicio("Supino", "Intermediário", "Peito"),
            Exercicio("Agachamento", "Intermediário", "Pernas"),
            Exercicio("Levantamento Terra", "Avançado", "Costas"),
            Exercicio("Rosca direta", "Iniciante", "Braços")
        )

        val exerciciosAdapter = ExercicioAdapter(listaDeExercicios) { exercicio ->
            Log.d("Menu de Administração do Exercício", "onMoreClick: " + exercicio.nome)
        }

        recyclerExercicios.layoutManager = LinearLayoutManager(requireContext())
        recyclerExercicios.adapter = exerciciosAdapter

    }
}