package com.example.unifor_gym.models

data class UsuarioGrupoTreino(
    val nome: String,
    val exercicios: List<UsuarioExercicioTreino>
)