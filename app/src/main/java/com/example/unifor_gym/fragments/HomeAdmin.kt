package com.example.unifor_gym.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.unifor_gym.R
import com.example.unifor_gym.activities.Login
import com.example.unifor_gym.adapters.DailyClassAdapter
import com.example.unifor_gym.adapters.PopularClassAdapter
import com.example.unifor_gym.models.PopularClass
import com.example.unifor_gym.models.DailyClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*

class HomeAdmin : Fragment() {

    private lateinit var rvDailyClasses: RecyclerView
    private lateinit var rvPopularClasses: RecyclerView
    private lateinit var tvTotalUsers: TextView
    private lateinit var tvTotalEquipments: TextView
    private lateinit var tvTotalClasses: TextView
    private lateinit var tvWelcome: TextView
    private lateinit var btnLogout: ImageButton

    private lateinit var dailyClassAdapter: DailyClassAdapter
    private lateinit var popularClassAdapter: PopularClassAdapter

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerViews()
        setupLogoutButton()
        loadDashboardData()
    }

    private fun initViews(view: View) {
        rvDailyClasses = view.findViewById(R.id.rvDailyClasses)
        rvPopularClasses = view.findViewById(R.id.rvPopularClasses)
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers)
        tvTotalEquipments = view.findViewById(R.id.tvTotalEquipments)
        tvTotalClasses = view.findViewById(R.id.tvTotalClasses)
        tvWelcome = view.findViewById(R.id.tvWelcome)
        btnLogout = view.findViewById(R.id.btnLogout)
    }

    private fun setupLogoutButton() {
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair")
            .setMessage("Tem certeza que deseja sair?")
            .setPositiveButton("Sim") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun performLogout() {
        try {
            // Fazer logout do Firebase
            auth.signOut()

            // Limpar preferências compartilhadas se houver
            val sharedPreferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().clear().apply()

            // Redirecionar para a tela de login
            redirectToLogin()

        } catch (e: Exception) {
            Log.e("HomeAdmin", "Erro ao fazer logout", e)
        }
    }

    private fun redirectToLogin() {
        // Substitua "LoginActivity" pelo nome da sua Activity de login
        val intent = Intent(requireContext(), Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setupRecyclerViews() {
        // Setup Daily Classes RecyclerView
        rvDailyClasses.layoutManager = LinearLayoutManager(context)
        dailyClassAdapter = DailyClassAdapter(emptyList()) // Iniciar com lista vazia
        rvDailyClasses.adapter = dailyClassAdapter

        // Setup Popular Classes RecyclerView
        rvPopularClasses.layoutManager = LinearLayoutManager(context)
        popularClassAdapter = PopularClassAdapter(emptyList()) // Iniciar com lista vazia
        rvPopularClasses.adapter = popularClassAdapter
    }

    private fun loadDashboardData() {
        loadTotalUsers()
        loadTotalEquipments()
        loadTotalClasses()
        loadCurrentUserName()
        loadPopularClasses()
        loadDailyClasses() // Adicionar carregamento das aulas diárias
    }

    // Nova função para carregar as aulas diárias do usuário logado
    private fun loadDailyClasses() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            db.collection("users")
                .whereEqualTo("email", user.email)
                .get()
                .addOnSuccessListener { userDocuments ->
                    if (!userDocuments.isEmpty) {
                        val userId = userDocuments.documents[0].id
                        val currentDayOfWeek = getCurrentDayOfWeek()

                        Log.d("HomeAdmin", "Buscando aulas para usuário: $userId no dia: $currentDayOfWeek")

                        // Buscar TODAS as aulas e filtrar manualmente
                        db.collection("Aulas")
                            .get()
                            .addOnSuccessListener { documents ->
                                val dailyClasses = mutableListOf<DailyClass>()

                                for (document in documents) {
                                    val nomeAula = document.getString("nome") ?: "Aula sem nome"

                                    // Múltiplas formas de tentar acessar o array
                                    val contemUsuario = try {
                                        // Tenta como ArrayList primeiro
                                        val arrayList = document.get("alunosMatriculados") as? ArrayList<*>
                                        val contemComoArrayList = arrayList?.any { it.toString() == userId } == true

                                        if (contemComoArrayList) {
                                            Log.d("HomeAdmin", "✓ Usuário encontrado como ArrayList na aula: $nomeAula")
                                            true
                                        } else {
                                            // Tenta como List
                                            val list = document.get("alunosMatriculados") as? List<*>
                                            val contemComoList = list?.any { it.toString() == userId } == true

                                            if (contemComoList) {
                                                Log.d("HomeAdmin", "✓ Usuário encontrado como List na aula: $nomeAula")
                                                true
                                            } else {
                                                Log.d("HomeAdmin", "✗ Usuário NÃO encontrado na aula: $nomeAula")
                                                Log.d("HomeAdmin", "   Array atual: ${document.get("alunosMatriculados")}")
                                                Log.d("HomeAdmin", "   Tipo do array: ${document.get("alunosMatriculados")?.javaClass?.simpleName}")
                                                false
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.e("HomeAdmin", "Erro ao verificar array de alunos para aula $nomeAula", e)
                                        false
                                    }

                                    if (contemUsuario) {
                                        val diaDaSemana = document.getString("diaDaSemana") ?: ""
                                        Log.d("HomeAdmin", "Verificando dia da semana: $diaDaSemana vs $currentDayOfWeek")

                                        // Verificar se a aula é hoje
                                        if (diaDaSemana.equals(currentDayOfWeek, ignoreCase = true)) {
                                            val dailyClass = DailyClass(
                                                name = nomeAula,
                                                timeRange = document.getString("horario") ?: "Horário não informado"
                                            )
                                            dailyClasses.add(dailyClass)
                                            Log.d("HomeAdmin", "✓ AULA ADICIONADA: $nomeAula - $diaDaSemana às ${document.getString("horario")}")
                                        }
                                    }
                                }

                                // Atualizar o adapter com as aulas do dia
                                updateDailyClassesAdapter(dailyClasses)
                                Log.d("HomeAdmin", "=== RESULTADO FINAL: ${dailyClasses.size} aulas para hoje ===")
                            }
                            .addOnFailureListener { exception ->
                                Log.w("HomeAdmin", "Erro ao carregar aulas diárias", exception)
                            }
                    } else {
                        Log.w("HomeAdmin", "Usuário não encontrado na base de dados")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("HomeAdmin", "Erro ao buscar dados do usuário", exception)
                }
        }
    }

    // Função para obter o dia da semana atual em português
    private fun getCurrentDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return when (dayOfWeek) {
            Calendar.SUNDAY -> "Domingo"
            Calendar.MONDAY -> "Segunda"
            Calendar.TUESDAY -> "Terca"
            Calendar.WEDNESDAY -> "Quarta"
            Calendar.THURSDAY -> "Quinta"
            Calendar.FRIDAY -> "Sexta"
            Calendar.SATURDAY -> "Sábado"
            else -> "Desconhecido"
        }
    }

    // Função para atualizar o adapter das aulas diárias
    private fun updateDailyClassesAdapter(dailyClasses: List<DailyClass>) {
        try {
            // Usar o método updateData do adapter
            dailyClassAdapter.updateData(dailyClasses)

            Log.d("HomeAdmin", "Adapter de aulas diárias atualizado com ${dailyClasses.size} aulas")

        } catch (e: Exception) {
            Log.e("HomeAdmin", "Erro ao atualizar adapter de aulas diárias", e)
        }
    }

    private fun loadPopularClasses() {
        db.collection("Aulas")
            .orderBy("qtdMatriculados", Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                val popularClasses = mutableListOf<PopularClass>()

                // Encontrar o maior número de matriculados para calcular a popularidade
                val maxEnrolled = documents.documents.firstOrNull()
                    ?.getLong("qtdMatriculados")?.toInt() ?: 1

                for (document in documents) {
                    val className = document.getString("nome") ?: "Aula sem nome"
                    val enrolledCount = document.getLong("qtdMatriculados")?.toInt() ?: 0
                    val instructor = document.getString("instrutor") ?: "Instrutor não informado"
                    val schedule = document.getString("horario") ?: "Horário não informado"

                    // Calcular popularidade como percentual (0-100) baseado no maior número
                    val popularity = if (maxEnrolled > 0) {
                        ((enrolledCount.toFloat() / maxEnrolled.toFloat()) * 100).toInt()
                    } else {
                        0
                    }

                    popularClasses.add(
                        PopularClass(
                            name = className,
                            popularity = popularity,
                            enrolledCount = enrolledCount,
                            instructor = instructor,
                            schedule = schedule
                        )
                    )
                }

                // Atualizar o adapter com as aulas populares
                popularClassAdapter.updateData(popularClasses)

                Log.d("HomeAdmin", "Popular classes loaded: ${popularClasses.size}")
                for (aula in popularClasses) {
                    Log.d("HomeAdmin", "Aula: ${aula.name}, Matriculados: ${aula.enrolledCount}, Popularidade: ${aula.popularity}%")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("HomeAdmin", "Error loading popular classes", exception)
            }
    }

    private fun loadTotalUsers() {
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                val totalUsers = documents.size()
                tvTotalUsers.text = totalUsers.toString()
                Log.d("HomeAdmin", "Total users: $totalUsers")
            }
            .addOnFailureListener { exception ->
                Log.w("HomeAdmin", "Error getting users count", exception)
                tvTotalUsers.text = "0"
            }
    }

    private fun loadCurrentUserName() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            db.collection("users")
                .whereEqualTo("email", user.email) // ou whereEqualTo("uid", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val userName = documents.documents[0].getString("name") ?: "Usuário"
                        tvWelcome.text = "Bem-vindo, $userName"
                    } else {
                        tvWelcome.text = "Bem-vindo"
                    }
                }
                .addOnFailureListener {
                    tvWelcome.text = "Bem-vindo"
                }
        }
    }

    private fun loadTotalEquipments() {
        db.collection("aparelhos")
            .get()
            .addOnSuccessListener { documents ->
                val totalEquipments = documents.size()
                tvTotalEquipments.text = totalEquipments.toString()
                Log.d("HomeAdmin", "Total equipments: $totalEquipments")
            }
            .addOnFailureListener { exception ->
                Log.w("HomeAdmin", "Error getting equipments count", exception)
                tvTotalEquipments.text = "0"
            }
    }

    private fun loadTotalClasses() {
        db.collection("Aulas")
            .get()
            .addOnSuccessListener { documents ->
                val totalClasses = documents.size()
                tvTotalClasses.text = totalClasses.toString()
                Log.d("HomeAdmin", "Total classes: $totalClasses")
            }
            .addOnFailureListener { exception ->
                Log.w("HomeAdmin", "Error getting classes count", exception)
                tvTotalClasses.text = "0"
            }
    }

    // Método para refresh dos dados
    fun refreshData() {
        loadDashboardData()
    }

    // Método para carregar todas as estatísticas de uma vez
    private fun loadAllStatistics() {
        var completedRequests = 0
        val totalRequests = 3

        val checkCompletion = {
            completedRequests++
            if (completedRequests == totalRequests) {
                Log.d("HomeAdmin", "All statistics loaded successfully")
            }
        }

        // Users
        db.collection("users").get()
            .addOnSuccessListener { documents ->
                tvTotalUsers.text = documents.size().toString()
                checkCompletion()
            }
            .addOnFailureListener {
                tvTotalUsers.text = "0"
                checkCompletion()
            }

        // Aparelhos
        db.collection("aparelhos").get()
            .addOnSuccessListener { documents ->
                tvTotalEquipments.text = documents.size().toString()
                checkCompletion()
            }
            .addOnFailureListener {
                tvTotalEquipments.text = "0"
                checkCompletion()
            }

        // Aulas
        db.collection("Aulas").get()
            .addOnSuccessListener { documents ->
                tvTotalClasses.text = documents.size().toString()
                checkCompletion()
            }
            .addOnFailureListener {
                tvTotalClasses.text = "0"
                checkCompletion()
            }
    }
}