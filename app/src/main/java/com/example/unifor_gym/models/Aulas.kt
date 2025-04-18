package com.example.unifor_gym.models

// Data class para Aulas Diárias
data class DailyClass(
    val name: String,
    val timeRange: String
)

// Data class para Aulas Populares
data class PopularClass(
    val name: String,
    val popularity: Int // 0-100
)

// Data class para Estatísticas do Dashboard
data class DashboardStats(
    val totalUsers: Int,
    val newUsers: Int,
    val totalEquipments: Int,
    val newEquipments: Int,
    val totalClasses: Int,
    val newClasses: Int
)
