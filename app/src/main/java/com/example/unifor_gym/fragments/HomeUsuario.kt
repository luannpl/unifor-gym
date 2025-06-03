package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.unifor_gym.R
import com.example.unifor_gym.models.ExercicioTreino
import com.example.unifor_gym.models.Treino
import com.google.firebase.firestore.FirebaseFirestore

class HomeUsuario : Fragment() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_usuario, container, false)

        val btnVerCostas = view.findViewById<Button>(R.id.btnVerCostas)
        val btnVerPeito = view.findViewById<Button>(R.id.btnVerPeito)

        btnVerCostas.setOnClickListener {
            buscarTreinoPorGrupo("Costas")
        }

        btnVerPeito.setOnClickListener {
            buscarTreinoPorGrupo("Peito")
        }

        return view
    }

    private fun buscarTreinoPorGrupo(grupo: String) {
        db.collection("treinos")
            .whereEqualTo("titulo", grupo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val titulo = doc.getString("titulo") ?: ""
                    val exerciciosRef = doc.reference.collection("exercicios")

                    exerciciosRef.get()
                        .addOnSuccessListener { result ->
                            val listaExercicios = mutableListOf<ExercicioTreino>()
                            for (ex in result) {
                                val exercicio = ex.toObject(ExercicioTreino::class.java)
                                listaExercicios.add(exercicio)
                            }

                            val treino = Treino(titulo, listaExercicios)

                            val fragment = UsuarioTreinoDetalhes().apply {
                                arguments = Bundle().apply {
                                    putParcelable("treinoSelecionado", treino)
                                }
                            }

                            parentFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(grupo)
                                .commit()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Erro ao buscar exercícios", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(requireContext(), "Treino não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao buscar treino", Toast.LENGTH_SHORT).show()
            }
    }
}
