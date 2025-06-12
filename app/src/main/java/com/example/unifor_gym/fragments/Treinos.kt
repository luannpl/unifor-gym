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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Treinos : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TreinoAdapterUser
    private val listaTreinos = mutableListOf<Treino>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_treinos, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewTreinos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TreinoAdapterUser(listaTreinos) { treinoClicado ->
            // FIXED: Proper navigation with exercise data
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

        buscarTreinosDoUsuario()

        return view
    }

    // FIXED: Load workouts for the current user only (matches your current approach)
    private fun buscarTreinosDoUsuario() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Log.e("Treinos", "Usuário não autenticado")
            buscarTreinosDoFirestore() // Fallback to all workouts if not authenticated
            return
        }

        val userEmail = currentUser.email

        if (userEmail.isNullOrBlank()) {
            Log.e("Treinos", "Email do usuário não encontrado")
            buscarTreinosDoFirestore() // Fallback to all workouts
            return
        }

        Log.d("Treinos", "Buscando treinos para o usuário: $userEmail")

        db.collection("treinos")
            .whereEqualTo("usuarioEmail", userEmail)
            .get()
            .addOnSuccessListener { result ->
                listaTreinos.clear()

                if (result.isEmpty) {
                    Log.d("Treinos", "Nenhum treino encontrado para o usuário")
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                Log.d("Treinos", "Encontrados ${result.size()} treinos")

                for (document in result) {
                    try {
                        val titulo = document.getString("titulo") ?: continue
                        val exerciciosMap = document["exercicios"] as? List<Map<String, Any>> ?: continue

                        Log.d("Treinos", "Processando treino: $titulo com ${exerciciosMap.size} exercícios")

                        val exercicios = exerciciosMap.mapNotNull { exercicioMap ->
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
                                Log.e("Treinos", "Erro ao converter exercício: ${exercicioMap}", e)
                                null
                            }
                        }

                        if (exercicios.isNotEmpty()) {
                            val treino = Treino(titulo, exercicios)
                            listaTreinos.add(treino)
                            Log.d("Treinos", "Treino '$titulo' adicionado com ${exercicios.size} exercícios")
                        }
                    } catch (e: Exception) {
                        Log.e("Treinos", "Erro ao processar documento: ${document.id}", e)
                    }
                }

                adapter.notifyDataSetChanged()
                Log.d("Treinos", "Total de treinos carregados: ${listaTreinos.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("Treinos", "Erro ao buscar treinos", exception)
            }
    }

    private fun buscarTreinosDoFirestore() {
        // Get current user for filtering
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        if (currentUser == null || userEmail.isNullOrBlank()) {
            Log.e("Treinos", "Usuário não autenticado ou email não encontrado")
            // Still show all workouts if user not authenticated (optional)
            loadAllWorkouts()
            return
        }

        // Filter workouts by user email
        db.collection("treinos")
            .whereEqualTo("usuarioEmail", userEmail)
            .get()
            .addOnSuccessListener { result ->
                listaTreinos.clear()

                if (result.isEmpty) {
                    adapter.notifyDataSetChanged()
                    return@addOnSuccessListener
                }

                for (document in result) {
                    val titulo = document.getString("titulo") ?: continue
                    val exerciciosMap = document["exercicios"] as? List<Map<String, Any>> ?: continue

                    val exercicios = exerciciosMap.mapNotNull { exercicioMap ->
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
                            Log.e("Treinos", "Erro ao converter exercício", e)
                            null
                        }
                    }

                    if (exercicios.isNotEmpty()) {
                        val treino = Treino(titulo, exercicios)
                        listaTreinos.add(treino)
                    }
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Treinos", "Erro ao buscar treinos do Firestore", exception)
            }
    }

    // Optional: Keep the original logic as a separate method if you want to show all workouts when user is not authenticated
    private fun loadAllWorkouts() {
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
                    val exerciciosMap = document["exercicios"] as? List<Map<String, Any>> ?: continue

                    val exercicios = exerciciosMap.mapNotNull { exercicioMap ->
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
                            Log.e("Treinos", "Erro ao converter exercício", e)
                            null
                        }
                    }

                    if (exercicios.isNotEmpty()) {
                        val treino = Treino(titulo, exercicios)
                        listaTreinos.add(treino)
                    }
                }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Treinos", "Erro ao buscar treinos do Firestore", exception)
            }
    }
}