package com.example.unifor_gym.models

data class UsuarioExercicioTreino(
    val id: Int,
    val nome: String,
    val carga: String,
    val repeticoes: String,
    val grupoMuscular: String,
    val equipamentos: List<String>,
    val dificuldade: String,
    val urlVideo: String? = null
)