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
import com.example.unifor_gym.models.ExercicioTreino
import com.example.unifor_gym.models.Treino
import com.example.unifor_gym.models.UsuarioExercicioTreino
import com.example.unifor_gym.models.UsuarioGrupoTreino
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioTreinos : Fragment(), UsuarioExercicioTreinoAdapter.OnExercicioTreinoListener {

    private lateinit var recyclerPerna: RecyclerView
    private lateinit var recyclerCosta: RecyclerView
    private lateinit var recyclerPeito: RecyclerView
    private val gruposTreino = mutableListOf<UsuarioGrupoTreino>()

    val auth = FirebaseAuth.getInstance()
    val usuarioAtual = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usuario_treinos, container, false)

        recyclerPerna = view.findViewById(R.id.recyclerPerna)
        recyclerPeito = view.findViewById(R.id.recyclerPeito)
        recyclerCosta = view.findViewById(R.id.recyclerCosta)

        carregarExerciciosFirebase()

        return view
    }

    private fun carregarExerciciosFirebase() {
        val db = FirebaseFirestore.getInstance()
        Log.d("UsuarioTreinos", "Iniciando carregamento dos exercícios do Firebase")
        val userEmail = FirebaseAuth.getInstance().currentUser?.email
        Log.d("UsuarioTreinos", "Email do usuário: $userEmail")

        db.collection("treinos")
            .whereEqualTo("usuarioEmail", userEmail)
            .get()
            .addOnSuccessListener { treinosSnapshot ->
                gruposTreino.clear()
                Log.d("UsuarioTreinos", "Número de grupos de treino encontrados: ${treinosSnapshot.size()}")
                if (treinosSnapshot.isEmpty) {
                    Log.d("UsuarioTreinos", "Nenhum grupo de treino encontrado")
                    setupRecycler()
                    return@addOnSuccessListener
                }

                for (treinoDoc in treinosSnapshot) {
                    val nomeGrupo = treinoDoc.getString("titulo") ?: ""
                    Log.d("UsuarioTreinos", "Processando grupo: $nomeGrupo")
                    val exerciciosRaw = treinoDoc.get("exercicios") as? List<Map<String, Any>> ?: continue

                    val exercicios = exerciciosRaw.mapNotNull { exercicioMap ->
                        try {
                            UsuarioExercicioTreino(
                                id = exercicioMap["nome"].hashCode(),
                                nome = exercicioMap["nome"]?.toString() ?: "",
                                carga = exercicioMap["carga"]?.toString() ?: "",
                                repeticoes = buildString {
                                    val series = exercicioMap["series"]?.toString() ?: "3"
                                    val reps = exercicioMap["repeticoes"]?.toString() ?: "10"
                                    append("${series}x${reps}")
                                },
                                grupoMuscular = nomeGrupo,
                                equipamentos = listOf(),
                                dificuldade = exercicioMap["dificuldade"]?.toString() ?: "Intermediário",
                                urlVideo = exercicioMap["urlVideo"]?.toString()
                            )
                        } catch (e: Exception) {
                            Log.e("UsuarioTreinos", "Erro ao converter exercício", e)
                            null
                        }
                    }

                    if (exercicios.isNotEmpty()) {
                        val grupoTreino = UsuarioGrupoTreino(nomeGrupo, exercicios)
                        gruposTreino.add(grupoTreino)
                    }
                }

                setupRecycler()
            }
            .addOnFailureListener { e ->
                Log.e("UsuarioTreinos", "Erro ao buscar treinos: ", e)
            }
    }

    private fun setupRecycler() {
        val grupos = listOf("Perna", "Costas", "Peito")

        for (grupo in grupos) {
            val grupoTreino = gruposTreino.find { it.nome.equals(grupo, ignoreCase = true) }
            grupoTreino?.let {
                Log.d("UsuarioTreinos", "Grupo '$grupo' encontrado com ${it.exercicios.size} exercícios")

                val adapterExercicios = UsuarioExercicioTreinoAdapter(it.exercicios, this)

                val recyclerView = when (grupo) {
                    "Perna" -> recyclerPerna
                    "Costas" -> recyclerCosta
                    "Peito" -> recyclerPeito
                    else -> null
                }

                recyclerView?.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = adapterExercicios
                }
            } ?: Log.w("UsuarioTreinos", "Grupo '$grupo' não encontrado")
        }
    }

    override fun onExercicioClick(exercicio: UsuarioExercicioTreino) {
        Log.d("UsuarioTreinos", "Exercício clicado: ${exercicio.nome} (ID: ${exercicio.id}) do grupo ${exercicio.grupoMuscular}")

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

        Log.d("UsuarioTreinos", "Navegando para detalhes do exercício")
    }

    // ADDED: Method to navigate to full workout view
    fun navigateToFullWorkout(grupoMuscular: String) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Log.e("UsuarioTreinos", "Usuário não autenticado")
            return
        }

        val userEmail = currentUser.email

        if (userEmail.isNullOrBlank()) {
            Log.e("UsuarioTreinos", "Email do usuário não encontrado")
            return
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("treinos")
            .whereEqualTo("usuarioEmail", userEmail)
            .whereEqualTo("titulo", grupoMuscular)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]
                    val titulo = document.getString("titulo") ?: grupoMuscular
                    val exerciciosData = document.get("exercicios") as? List<Map<String, Any>> ?: emptyList()

                    // Convert exercises to ExercicioTreino for TreinoDetalhes
                    val exercicios = exerciciosData.mapNotNull { exercicioMap ->
                        try {
                            ExercicioTreino(
                                id = exercicioMap["nome"]?.toString() ?: "",
                                nome = exercicioMap["nome"]?.toString() ?: "",
                                peso = exercicioMap["carga"]?.toString() ?: "",
                                repeticoes = buildString {
                                    val series = exercicioMap["series"]?.toString() ?: "3"
                                    val reps = exercicioMap["repeticoes"]?.toString() ?: "10"
                                    append("${series}x${reps}")
                                },
                                grupoMuscular = titulo,
                                descricao = "Descanso: ${exercicioMap["descanso"]?.toString() ?: "60s"}"
                            )
                        } catch (e: Exception) {
                            Log.e("UsuarioTreinos", "Erro ao converter exercício", e)
                            null
                        }
                    }

                    val treino = Treino(titulo, exercicios)

                    // Navigate to TreinoDetalhes
                    val fragment = TreinoDetalhes().apply {
                        arguments = Bundle().apply {
                            putString("treino_nome", treino.titulo)
                            putParcelableArrayList("exercicios", ArrayList(treino.exercicios))
                        }
                    }

                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(grupoMuscular)
                        .commit()

                    Log.d("UsuarioTreinos", "Navegando para treino completo de $grupoMuscular")
                } else {
                    Log.w("UsuarioTreinos", "Nenhum treino encontrado para $grupoMuscular")
                }
            }
            .addOnFailureListener { e ->
                Log.e("UsuarioTreinos", "Erro ao buscar treino de $grupoMuscular", e)
            }
    }
}