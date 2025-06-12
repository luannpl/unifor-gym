package com.example.unifor_gym.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
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
    private lateinit var txtTituloTreino: TextView
    private lateinit var btnVoltar: androidx.cardview.widget.CardView
    private val db = FirebaseFirestore.getInstance()
    private val exercicios = mutableListOf<ExercicioTreino>()
    private lateinit var adapter: ExercicioAdapterUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_treino_detalhes, container, false)



        // Initialize views using the correct IDs from your layout
        recyclerExercicios = view.findViewById(R.id.recyclerExerciciosTreino)
        txtTituloTreino = view.findViewById(R.id.txtNomeTreinoDetalhes)
        btnVoltar = view.findViewById(R.id.btnVoltarTreinoDetalhes)

        // Setup RecyclerView
        recyclerExercicios.layoutManager = LinearLayoutManager(requireContext())

        adapter = ExercicioAdapterUser(exercicios) { exercicio ->
            // Navigate to exercise details
            val fragment = UsuarioTreinoDetalhes().apply {
                arguments = Bundle().apply {
                    putString("grupoMuscular", exercicio.grupoMuscular)
                    putString("exercicioNome", exercicio.nome)
                    putString("exercicioId", exercicio.id)
                }
            }
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerExercicios.adapter = adapter

        // Setup back button - using CardView click instead of ImageButton
        btnVoltar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Load data from arguments
        loadDataFromArguments()

        return view
    }

    private fun loadDataFromArguments() {
        val treinoNome = arguments?.getString("treino_nome")
        val exerciciosFromBundle = arguments?.getParcelableArrayList<ExercicioTreino>("exercicios")

        Log.d("TreinoDetalhes", "Recebido treino: $treinoNome")
        Log.d("TreinoDetalhes", "Exercícios recebidos: ${exerciciosFromBundle?.size ?: 0}")

        // Set title
        txtTituloTreino.text = treinoNome?.uppercase() ?: "TREINO"

        // Load exercises
        if (exerciciosFromBundle != null && exerciciosFromBundle.isNotEmpty()) {
            exercicios.clear()
            exercicios.addAll(exerciciosFromBundle)
            adapter.notifyDataSetChanged()
            Log.d("TreinoDetalhes", "Exercícios carregados: ${exercicios.size}")
        } else {
            // If no exercises received from bundle, try to load from treinoId (fallback)
            val treinoId = arguments?.getString("treinoId")
            treinoId?.let { id ->
                loadExerciciosFromFirestore(id)
            }
        }
    }

    private fun loadExerciciosFromFirestore(treinoId: String) {
        Log.d("TreinoDetalhes", "Carregando exercícios do Firestore para treino: $treinoId")

        db.collection("treinos").document(treinoId).collection("exercicios")
            .get()
            .addOnSuccessListener { result ->
                exercicios.clear()
                for (doc in result) {
                    try {
                        val exercicio = doc.toObject(ExercicioTreino::class.java)
                        exercicios.add(exercicio)
                    } catch (e: Exception) {
                        Log.e("TreinoDetalhes", "Erro ao converter exercício", e)
                    }
                }
                adapter.notifyDataSetChanged()
                Log.d("TreinoDetalhes", "Exercícios carregados do Firestore: ${exercicios.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("TreinoDetalhes", "Erro ao carregar exercícios", exception)
                Toast.makeText(requireContext(), "Erro ao carregar exercícios: ${exception.message}", Toast.LENGTH_LONG).show()
            }
    }
}