package com.example.unifor_gym.fragments

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore

class Treinos : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TreinoAdapterUser
    private val listaTreinos = mutableListOf<Treino>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_treinos, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewTreinos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TreinoAdapterUser(listaTreinos) { treinoClicado ->
            val bundle = Bundle().apply {
                putString("treino_nome", treinoClicado.titulo)
                putParcelableArrayList("exercicios", ArrayList(treinoClicado.exercicios))
            }

            val treinoDetalhesFragment = TreinoDetalhes().apply {
                arguments = bundle
            }

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, treinoDetalhesFragment)
                .addToBackStack(null)
                .commit()
        }

        recyclerView.adapter = adapter

        buscarTreinosDoFirestore()

        return view
    }

    private fun buscarTreinosDoFirestore() {
        db.collection("treinos")
            .get()
            .addOnSuccessListener { result ->
                listaTreinos.clear()

                if (result.isEmpty) {
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                for (document in result) {
                    val titulo = document.getString("titulo") ?: continue
                    val exerciciosMap =
                        document["exercícios"] as? List<Map<String, Any>> ?: emptyList()

                    if (exerciciosMap.isEmpty()) {
                        // Se não tem exercícios, adiciona treino vazio logo
                        listaTreinos.add(Treino(titulo, emptyList()))
                        adapter.notifyDataSetChanged()
                        continue
                    }

                    val exerciciosList = mutableListOf<ExercicioTreino>()
                    var exerciciosCarregados = 0

                    for (exercicioMap in exerciciosMap) {
                        val idExercicio = exercicioMap["nome"]?.toString()
                            ?: exercicioMap["id"]?.toString()
                            ?: continue

                        val peso = exercicioMap["carga"]?.toString() ?: ""
                        val repeticoes = exercicioMap["repetições"]?.toString() ?: ""

                        db.collection("exercicios").document(idExercicio).get()
                            .addOnSuccessListener { exercicioDoc ->
                                val nomeExercicio = exercicioDoc.getString("nome") ?: "Exercício"
                                exerciciosList.add(ExercicioTreino(nomeExercicio, peso, repeticoes))
                                exerciciosCarregados++

                                // Só adiciona o treino quando todos os exercícios estiverem carregados
                                if (exerciciosCarregados == exerciciosMap.size) {
                                    listaTreinos.add(Treino(titulo, exerciciosList))
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("Treinos", "Erro ao buscar nome do exercício", e)
                                exerciciosCarregados++
                                if (exerciciosCarregados == exerciciosMap.size) {
                                    listaTreinos.add(Treino(titulo, exerciciosList))
                                    adapter.notifyDataSetChanged()
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Treinos", "Erro ao buscar treinos", exception)
            }
    }
}