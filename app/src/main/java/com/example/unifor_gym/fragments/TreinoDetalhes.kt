package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.ExercicioDetalhesAdapter
import com.example.unifor_gym.models.Exercicio

class TreinoDetalhes : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var txtTituloTreino: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_treino_detalhes, container, false)

        // Inicializar views
        recyclerView = view.findViewById(R.id.recyclerViewExercicios)
        txtTituloTreino = view.findViewById(R.id.txtTituloTreino)

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Obter argumentos passados pelo fragmento Treinos
        arguments?.let { args ->
            val treinoNome = args.getString("treino_nome", "")
            val exercicios = args.getParcelableArrayList<Exercicio>("exercicios") ?: ArrayList()

            // Atualizar título
            txtTituloTreino.text = treinoNome

            // Configurar adapter
            recyclerView.adapter = ExercicioDetalhesAdapter(exercicios.toList())
        }

        // Configurar botão de voltar
        view.findViewById<View>(R.id.btnVoltar).setOnClickListener {
            // Opção 1: Usando FragmentManager (sem Navigation Component)
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}