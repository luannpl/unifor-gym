package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.Handler
import android.os.Looper
import com.example.unifor_gym.ApiService
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.DailyClassAdapter
import com.example.unifor_gym.adapters.PopularClassAdapter

class HomeAdmin : Fragment() {

    private lateinit var rvDailyClasses: RecyclerView
    private lateinit var rvPopularClasses: RecyclerView
    private lateinit var tvTotalUsers: TextView
    private lateinit var tvTotalEquipments: TextView
    private lateinit var tvTotalClasses: TextView

    private lateinit var dailyClassAdapter: DailyClassAdapter
    private lateinit var popularClassAdapter: PopularClassAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar views
        rvDailyClasses = view.findViewById(R.id.rvDailyClasses)
        rvPopularClasses = view.findViewById(R.id.rvPopularClasses)
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers)
        tvTotalEquipments = view.findViewById(R.id.tvTotalEquipments)
        tvTotalClasses = view.findViewById(R.id.tvTotalClasses)

        // Configurar RecyclerViews
        setupDailyClassesRecyclerView()
        setupPopularClassesRecyclerView()

        // Carregar dados (simular chamada de API)
        loadDashboardData()
    }

    private fun setupDailyClassesRecyclerView() {
        rvDailyClasses.layoutManager = LinearLayoutManager(context)
        dailyClassAdapter = DailyClassAdapter(ApiService.getDailyClasses())
        rvDailyClasses.adapter = dailyClassAdapter
    }

    private fun setupPopularClassesRecyclerView() {
        rvPopularClasses.layoutManager = LinearLayoutManager(context)
        popularClassAdapter = PopularClassAdapter(ApiService.getPopularClasses())
        rvPopularClasses.adapter = popularClassAdapter
    }

    private fun loadDashboardData() {
        // Simular atraso de API
        Handler(Looper.getMainLooper()).postDelayed({
            // Obter dados da API simulada
            val stats = ApiService.getDashboardStats()
            val dailyClasses = ApiService.getDailyClasses()
            val popularClasses = ApiService.getPopularClasses()

            // Atualizar a UI
            tvTotalUsers.text = stats.totalUsers.toString()
            tvTotalEquipments.text = stats.totalEquipments.toString()
            tvTotalClasses.text = stats.totalClasses.toString()

            // Atualizar RecyclerViews
            dailyClassAdapter.updateData(dailyClasses)
            popularClassAdapter.updateData(popularClasses)
        }, 500) // Simular atraso de 500ms
    }

    // Método para atualizar dados (para uso futuro caso queira implementar refresh)
    fun refreshData() {
        loadDashboardData()
    }
}