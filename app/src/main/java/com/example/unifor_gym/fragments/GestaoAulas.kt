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
import com.example.unifor_gym.adapters.AulaAdapter
import com.example.unifor_gym.adapters.ExercicioAdapter

data class Aula (
    val nome: String,
    val qtdMatriculados: String,
    val qtdVagas: String
)

class GestaoAulas : Fragment() {
    private lateinit var recyclerAulas: RecyclerView
    private lateinit var listaDeAulas: List<Aula>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gestao_aulas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerAulas = view.findViewById(R.id.recyclerAulas)

        listaDeAulas = listOf(
            Aula("Yoga", "12", "15"),
            Aula("Crossfit", "13", "20"),
            Aula("Rumba", "10", "25"),
            Aula("Pilates", "5", "15"),
        )

        val aulasAdapter = AulaAdapter(listaDeAulas) { aula ->
            Log.d("Menu de Administração do Exercício", "onMoreClick: " + aula.nome)
        }

        recyclerAulas.layoutManager = LinearLayoutManager(requireContext())
        recyclerAulas.adapter = aulasAdapter
    }

}