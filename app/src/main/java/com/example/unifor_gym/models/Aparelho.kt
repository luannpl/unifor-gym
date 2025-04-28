package com.example.unifor_gym.models

data class Aparelho(
    val id: Int,
    val nome: String,
    val tipo: String, // "Cardio" ou "Muscul."
    var status: String // "Operacional" ou "Manutenção"
)