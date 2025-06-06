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
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth


class UsuarioTreinos : Fragment(), UsuarioExercicioTreinoAdapter.OnExercicioTreinoListener {

    private lateinit var recyclerPerna: RecyclerView
    private lateinit var recyclerPeito: RecyclerView
    private lateinit var recyclerCosta: RecyclerView
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

                var gruposCarregados = 0
                val totalGrupos = treinosSnapshot.size()

                for (treinoDoc in treinosSnapshot) {
                    val nomeGrupo = treinoDoc.getString("titulo") ?: ""
                    Log.d("UsuarioTreinos", "Processando grupo: $nomeGrupo")
                    val exerciciosRaw = treinoDoc.get("exercicios") as? List<Map<String, Any>> ?: emptyList()
                    Log.d("UsuarioTreinos", "Exercícios do grupo raw: $exerciciosRaw")
                    val exerciciosLista = exerciciosRaw.mapNotNull { it["nome"] as? String }
                    Log.d("UsuarioTreinos", "Exercícios do grupo convertidos: $exerciciosLista")

                    val exerciciosTreino = mutableListOf<UsuarioExercicioTreino>()

                    if (exerciciosLista.isEmpty()) {
                        Log.d("UsuarioTreinos", "Grupo $nomeGrupo não tem exercícios")
                        gruposTreino.add(UsuarioGrupoTreino(nomeGrupo, exerciciosTreino))
                        gruposCarregados++
                        if (gruposCarregados == totalGrupos) {
                            Log.d("UsuarioTreinos", "Todos os grupos carregados - atualizando RecyclerView")
                            setupRecycler()
                        }
                        continue
                    }

                    var exerciciosCarregados = 0
                    val totalExercicios = exerciciosLista.size

                    for (nomeExercicio in exerciciosLista) {
                        val exercicioInfo = exerciciosRaw.find { it["nome"] == nomeExercicio }
                        val carga = exercicioInfo?.get("carga") as? String ?: ""
                        val repeticoes = exercicioInfo?.get("repeticoes") as? String ?: ""
                        Log.d("UsuarioTreinos", "Buscando dados do exercício: $nomeExercicio")
                        db.collection("exercicios")
                            .whereEqualTo("nome", nomeExercicio)
                            .get()
                            .addOnSuccessListener { exercicioSnapshot ->
                                if (!exercicioSnapshot.isEmpty) {
                                    val doc = exercicioSnapshot.documents[0]
                                    Log.d("UsuarioTreinos", "Exercício encontrado: ${doc.getString("nome")}")

                                    val aparelhosRaw = doc.get("aparelhos")
                                    Log.d("UsuarioTreinos", "Campo aparelhos raw: $aparelhosRaw, tipo: ${aparelhosRaw?.javaClass}")

                                    val usuarioExercicio = UsuarioExercicioTreino(
                                        id = doc.getLong("id")?.toInt() ?: 0,
                                        nome = doc.getString("nome") ?: "",
                                        carga = carga ?: "",
                                        repeticoes = repeticoes ?: "",
                                        grupoMuscular = doc.getString("grupoMuscular") ?: "",
                                        equipamentos = doc.get("aparelhos") as? List<String> ?: emptyList(),
                                        dificuldade = doc.getString("dificuldade") ?: "",
                                        urlVideo = doc.getString("urlVideo")
                                    )
                                    exerciciosTreino.add(usuarioExercicio)
                                } else {
                                    Log.w("UsuarioTreinos", "Nenhum documento encontrado para exercício: $nomeExercicio")
                                }
                                exerciciosCarregados++
                                Log.d("UsuarioTreinos", "Exercícios carregados no grupo '$nomeGrupo': $exerciciosCarregados/$totalExercicios")

                                if (exerciciosCarregados == totalExercicios) {
                                    Log.d("UsuarioTreinos", "Todos exercícios do grupo '$nomeGrupo' carregados, adicionando ao gruposTreino")
                                    gruposTreino.add(UsuarioGrupoTreino(nomeGrupo, exerciciosTreino))
                                    gruposCarregados++
                                    if (gruposCarregados == totalGrupos) {
                                        Log.d("UsuarioTreinos", "Todos os grupos carregados - atualizando RecyclerView")
                                        setupRecycler()
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e("UsuarioTreinos", "Erro ao carregar exercício $nomeExercicio: ", e)
                                exerciciosCarregados++
                                if (exerciciosCarregados == totalExercicios) {
                                    gruposTreino.add(UsuarioGrupoTreino(nomeGrupo, exerciciosTreino))
                                    gruposCarregados++
                                    if (gruposCarregados == totalGrupos) {
                                        Log.d("UsuarioTreinos", "Todos os grupos carregados - atualizando RecyclerView")
                                        setupRecycler()
                                    }
                                }
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("UsuarioTreinos", "Erro ao buscar treinos: ", e)
            }
    }


    private fun setupRecycler() {
        val grupos = listOf("Perna", "Costa", "Peito")

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
}
