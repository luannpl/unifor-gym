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
import com.example.unifor_gym.adapters.UsuarioExercicioTreinoAdapter
import com.example.unifor_gym.models.UsuarioExercicioTreino
import com.example.unifor_gym.models.UsuarioGrupoTreino
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast


class UsuarioTreinos : Fragment(), UsuarioExercicioTreinoAdapter.OnExercicioTreinoListener {

    private lateinit var recyclerPerna: RecyclerView
    private val gruposTreino = mutableListOf<UsuarioGrupoTreino>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usuario_treinos, container, false)

        recyclerPerna = view.findViewById(R.id.recyclerPerna)

        carregarExerciciosFirebase()

        return view
    }

    private fun carregarExerciciosFirebase() {
        val db = FirebaseFirestore.getInstance()

        db.collection("exercicios")
            .get()
            .addOnSuccessListener { result ->
                val mapaGrupo = mutableMapOf<String, MutableList<UsuarioExercicioTreino>>()

                for (document in result) {
                    val nome = document.getString("nome") ?: continue
                    val grupoMuscular = document.getString("grupoMuscular")?.trim() ?: "Outro"
                    val instrucoes = document.getString("instruções") ?: ""
                    val dificuldade = document.getString("dificuldade") ?: "Desconhecida"

                    val exercicio = UsuarioExercicioTreino(
                        id = nome.hashCode(),
                        nome = nome,
                        carga = "Indefinida",
                        repeticoes = "3x10",
                        grupoMuscular = grupoMuscular,
                        equipamentos = emptyList(),
                        dificuldade = dificuldade,
                        organizacao = "Organizado"
                    )

                    val lista = mapaGrupo.getOrPut(grupoMuscular) { mutableListOf() }
                    lista.add(exercicio)
                }

                gruposTreino.clear()
                for ((grupo, listaExercicios) in mapaGrupo) {
                    gruposTreino.add(UsuarioGrupoTreino(grupo, listaExercicios))
                }

                setupRecycler()
            }
            .addOnFailureListener { exception ->
                Log.e("UsuarioTreinos", "Erro ao carregar exercícios", exception)
                Toast.makeText(requireContext(), "Falha ao carregar exercícios.", Toast.LENGTH_SHORT).show()
            }

    }

    private fun setupRecycler() {
        val perna = gruposTreino.find { it.nome.equals("Perna", ignoreCase = true) }
        if (perna != null) {
            val adapter = UsuarioExercicioTreinoAdapter(perna.exercicios, this)
            recyclerPerna.layoutManager = LinearLayoutManager(requireContext())
            recyclerPerna.adapter = adapter
        }
    }

    override fun onExercicioClick(exercicio: UsuarioExercicioTreino) {
        val detalhesFragment = UsuarioTreinoDetalhes().apply {
            arguments = Bundle().apply {
                putString("grupoMuscular", exercicio.grupoMuscular)
                putInt("exercicioId", exercicio.id)
            }
        }

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detalhesFragment)
            .addToBackStack(null)
            .commit()
    }
}
