package com.example.unifor_gym.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
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

    // Views
    private lateinit var tvBemVindo: TextView
    private lateinit var tvTituloTreino: TextView
    private lateinit var exerciciosContainer: LinearLayout
    private lateinit var layoutSemTreino: LinearLayout
    private lateinit var btnVerCostas: Button
    private lateinit var btnVerPeito: Button

    // Data
    private var treinoAtual: Treino? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_usuario, container, false)

        // Inicializar os componentes
        initializeViews(view)
        setupClickListeners()

        // Carregar dados do usuário e treino
        carregarNomeUsuario()
        carregarTreinoDoUsuario()

        return view
    }

    private fun initializeViews(view: View) {
        tvBemVindo = view.findViewById(R.id.tvBemVindo)
        tvTituloTreino = view.findViewById(R.id.tvTituloTreino)
        btnVerCostas = view.findViewById(R.id.btnVerCostas)
        btnVerPeito = view.findViewById(R.id.btnVerPeito)
        exerciciosContainer = view.findViewById(R.id.layoutExercicios)
        layoutSemTreino = view.findViewById(R.id.layoutSemTreino)
    }

    private fun setupClickListeners() {
        btnVerCostas.setOnClickListener {
            buscarTreinoPorGrupo("Costas")
        }

        btnVerPeito.setOnClickListener {
            buscarTreinoPorGrupo("Peito")
        }
    }

    private fun carregarNomeUsuario() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Buscar dados do usuário na coleção "users" do Firestore
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nomeUsuario = document.getString("name") ?:
                        document.getString("nome") ?:
                        currentUser.displayName ?:
                        "Usuário"

                        tvBemVindo.text = "Bem-vindo, $nomeUsuario"
                    } else {
                        tvBemVindo.text = "Bem-vindo, ${currentUser.displayName ?: "Usuário"}"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeUsuario", "Erro ao carregar nome do usuário", exception)
                    tvBemVindo.text = "Bem-vindo, Usuário"
                }
        } else {
            tvBemVindo.text = "Bem-vindo, Usuário"
        }
    }

    private fun carregarTreinoDoUsuario() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            mostrarTreinoVazio()
            return
        }

        val userEmail = currentUser.email

        if (userEmail.isNullOrBlank()) {
            mostrarTreinoVazio()
            return
        }

        // Buscar um treino qualquer do usuário para exibição na home
        db.collection("treinos")
            .whereEqualTo("usuarioEmail", userEmail)
            .limit(1) // Pegar apenas o primeiro treino
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]

                    val titulo = document.getString("titulo") ?: "Treino"
                    val exerciciosData = document.get("exercicios") as? List<Map<String, Any>> ?: emptyList()

                    // Converter os exercícios
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
                            Log.e("HomeUsuario", "Erro ao converter exercício", e)
                            null
                        }
                    }

                    treinoAtual = Treino(titulo, exercicios)
                    mostrarTreino(treinoAtual!!)

                } else {
                    Log.d("HomeUsuario", "Nenhum treino encontrado para o email: $userEmail")
                    mostrarTreinoVazio()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HomeUsuario", "Erro ao buscar treinos do usuário", exception)
                mostrarTreinoVazio()
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar treino: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun mostrarTreino(treino: Treino) {
        // Atualizar o título do treino
        tvTituloTreino.text = treino.titulo.uppercase()

        // Limpar container de exercícios
        exerciciosContainer.removeAllViews()

        // Esconder mensagem de "sem treino"
        layoutSemTreino.visibility = View.GONE

        Log.d("HomeUsuario", "Mostrando treino: ${treino.titulo} com ${treino.exercicios.size} exercícios")

        // Adicionar cada exercício dinamicamente
        treino.exercicios.forEachIndexed { index, exercicio ->
            val exercicioView = criarViewExercicio(exercicio, index % 2 == 1)
            exerciciosContainer.addView(exercicioView)

            // Adicionar linha divisória (exceto no último)
            if (index < treino.exercicios.size - 1) {
                val divider = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                    )
                    setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
                }
                exerciciosContainer.addView(divider)
            }
        }
    }

    private fun criarViewExercicio(exercicio: ExercicioTreino, isEven: Boolean): LinearLayout {
        val exercicioLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(16, 12, 16, 12)

            if (isEven) {
                setBackgroundColor(resources.getColor(android.R.color.background_light, null))
            }
        }

        val nomeView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = exercicio.nome
            setTextColor(resources.getColor(android.R.color.black, null))
            textSize = 14f
        }

        val pesoView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = exercicio.peso
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(resources.getColor(android.R.color.black, null))
            textSize = 14f
        }

        val seriesView = TextView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = exercicio.repeticoes
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextColor(resources.getColor(android.R.color.black, null))
            textSize = 14f
        }

        exercicioLayout.addView(nomeView)
        exercicioLayout.addView(pesoView)
        exercicioLayout.addView(seriesView)

        return exercicioLayout
    }

    private fun mostrarTreinoVazio() {
        tvTituloTreino.text = "NENHUM TREINO"

        // Limpar container de exercícios
        exerciciosContainer.removeAllViews()

        // Mostrar mensagem de "sem treino"
        layoutSemTreino.visibility = View.VISIBLE

        Log.d("HomeUsuario", "Nenhum treino encontrado - mostrando layout vazio")
    }

    // FIXED: This method now properly navigates to TreinoDetalhes
    private fun buscarTreinoPorGrupo(grupo: String) {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val userEmail = currentUser.email

        if (userEmail.isNullOrBlank()) {
            Toast.makeText(requireContext(), "Email do usuário não encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        // Buscar treino específico do usuário por grupo muscular
        db.collection("treinos")
            .whereEqualTo("usuarioEmail", userEmail)
            .whereEqualTo("titulo", grupo)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0]

                    val titulo = document.getString("titulo") ?: grupo
                    val exerciciosData = document.get("exercicios") as? List<Map<String, Any>> ?: emptyList()

                    // Converter os exercícios
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
                            Log.e("HomeUsuario", "Erro ao converter exercício", e)
                            null
                        }
                    }

                    val treino = Treino(titulo, exercicios)

                    // FIXED: Navigate to TreinoDetalhes with proper bundle
                    val fragment = TreinoDetalhes().apply {
                        arguments = Bundle().apply {
                            putString("treino_nome", treino.titulo)
                            putParcelableArrayList("exercicios", ArrayList(treino.exercicios))
                        }
                    }

                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(grupo)
                        .commit()

                } else {
                    Toast.makeText(
                        requireContext(),
                        "Treino de $grupo não encontrado",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HomeUsuario", "Erro ao buscar treino de $grupo", exception)
                Toast.makeText(
                    requireContext(),
                    "Erro ao buscar treino: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onResume() {
        super.onResume()
        // Recarregar dados quando o fragment volta a estar ativo
        carregarTreinoDoUsuario()
    }
}