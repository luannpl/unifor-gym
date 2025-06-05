package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.unifor_gym.R
import com.example.unifor_gym.models.ExercicioTreino
import com.example.unifor_gym.models.Treino
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeUsuario : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var tvBemVindo: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_usuario, container, false)

        // Inicializar os componentes
        tvBemVindo = view.findViewById(R.id.tvBemVindo)
        val btnVerCostas = view.findViewById<Button>(R.id.btnVerCostas)
        val btnVerPeito = view.findViewById<Button>(R.id.btnVerPeito)

        // Carregar nome do usuário
        carregarNomeUsuario()

        btnVerCostas.setOnClickListener {
            buscarTreinoPorGrupo("Costas")
        }

        btnVerPeito.setOnClickListener {
            buscarTreinoPorGrupo("Peito")
        }

        return view
    }

    private fun carregarNomeUsuario() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Buscar dados do usuário na coleção "usuarios" do Firestore
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nomeUsuario = document.getString("name") ?:
                        document.getString("name") ?:
                        currentUser.displayName ?:
                        "Usuário"

                        tvBemVindo.text = "Bem vindo, $nomeUsuario"
                    } else {
                        // Se não encontrar no Firestore, usar o displayName do Firebase Auth
                        val nome = currentUser.displayName ?: "Usuário"
                        tvBemVindo.text = "Bem vindo, $nome"
                    }
                }
                .addOnFailureListener { exception ->
                    // Em caso de erro, usar dados do Firebase Auth ou padrão
                    val nome = currentUser.displayName ?:
                    currentUser.email?.substringBefore("@") ?:
                    "Usuário"
                    tvBemVindo.text = "Bem vindo, $nome"

                    // Log do erro (opcional)
                    println("Erro ao carregar dados do usuário: ${exception.message}")
                }
        } else {
            // Usuário não está logado
            tvBemVindo.text = "Bem vindo"
        }
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