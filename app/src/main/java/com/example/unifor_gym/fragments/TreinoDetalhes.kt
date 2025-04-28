package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.ExercicioAdapterUser
import com.example.unifor_gym.models.ExercicioTreino

class TreinoDetalhes : Fragment() {

    private lateinit var txtTreinoNome: TextView
    private lateinit var btnVoltar: CardView
    private lateinit var recyclerExercicios: RecyclerView
    private var exercicios: List<ExercicioTreino> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_treino_detalhes, container, false)

        txtTreinoNome = view.findViewById(R.id.txtNomeTreinoDetalhes)
        btnVoltar = view.findViewById(R.id.btnVoltarTreinoDetalhes)
        recyclerExercicios = view.findViewById(R.id.recyclerExerciciosTreino)

        // Recuperar argumentos
        arguments?.let { args ->
            val treinoNome = args.getString("treino_nome", "")
            exercicios = args.getParcelableArrayList("exercicios") ?: emptyList()

            txtTreinoNome.text = treinoNome
        }

        // Configurar RecyclerView
        recyclerExercicios.layoutManager = LinearLayoutManager(requireContext())
        recyclerExercicios.adapter = ExercicioAdapterUser(exercicios)

        // Configurar botão voltar
        btnVoltar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}