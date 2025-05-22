package com.example.unifor_gym.models

enum class UserRole {
    USER,
    ADMIN
}

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.USER,
    val createdAt: Long = System.currentTimeMillis()
)