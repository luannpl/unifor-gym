package com.example.unifor_gym

import com.example.unifor_gym.models.DailyClass
import com.example.unifor_gym.models.DashboardStats
import com.example.unifor_gym.models.PopularClass

object ApiService {

    // Método para simular a obtenção de aulas diárias
    fun getDailyClasses(): List<DailyClass> {
        return listOf(
            DailyClass("Zumba", "09:00 - 09:50"),
            DailyClass("Jiu-jitsu", "13:00 - 15:00"),
            DailyClass("Futebol", "16:00 - 18:00")
        )
    }

    // Método para simular a obtenção de aulas populares
    fun getPopularClasses(): List<PopularClass> {
        return listOf(
            PopularClass("Zumba", 100),
            PopularClass("Crossfit", 85),
            PopularClass("Yoga", 70),
            PopularClass("Spinning", 60),
            PopularClass("Boxe", 50)
        )
    }

    // Método para simular a obtenção de estatísticas
    fun getDashboardStats(): DashboardStats {
        return DashboardStats(
            totalUsers = 1500,
            newUsers = 300,
            totalEquipments = 102,
            newEquipments = 30,
            totalClasses = 36,
            newClasses = 7
        )
    }
}