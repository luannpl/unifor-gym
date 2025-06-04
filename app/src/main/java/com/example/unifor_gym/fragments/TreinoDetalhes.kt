package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.ExercicioAdapterUser
import com.example.unifor_gym.models.ExercicioTreino
import com.google.firebase.firestore.FirebaseFirestore

class TreinoDetalhes : Fragment() {

    private lateinit var recyclerExercicios: RecyclerView
    private val db = FirebaseFirestore.getInstance()
    private var treinoId: String? = null
    private val exercicios = mutableListOf<ExercicioTreino>()
    private lateinit var adapter: ExercicioAdapterUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_treino_detalhes, container, false)

        recyclerExercicios = view.findViewById(R.id.recyclerExerciciosTreino)
        recyclerExercicios.layoutManager = LinearLayoutManager(requireContext())

        adapter = ExercicioAdapterUser(exercicios) { exercicio ->
            val fragment = UsuarioTreinoDetalhes().apply {
                arguments = Bundle().apply {
                    putString("grupoMuscular", exercicio.grupoMuscular)
                    putString("exercicioId", exercicio.id)
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerExercicios.adapter = adapter

        // Recuperar treinoId dos argumentos
        treinoId = arguments?.getString("treinoId")

        // Buscar exercícios do treino no Firestore
        treinoId?.let { id ->
            db.collection("treinos").document(id).collection("exercicios")
                .get()
                .addOnSuccessListener { result ->
                    exercicios.clear()
                    for (doc in result) {
                        val exercicio = doc.toObject(ExercicioTreino::class.java)
                        exercicios.add(exercicio)
                    }
                    adapter.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Erro ao carregar exercícios: ${exception.message}", Toast.LENGTH_LONG).show()
                }
        }

        return view
    }
}
