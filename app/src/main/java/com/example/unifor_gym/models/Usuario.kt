package com.example.unifor_gym.models

data class Usuario(
    val id: Int,
    val nome: String,
    val email: String = "",
    val corAvatar: Int
)