package com.example.unifor_gym.models

data class PopularClass(
    val name: String,
    val popularity: Int, // Percentage (0-100)
    val enrolledCount: Int, // Number of enrolled students
    val instructor: String,
    val schedule: String
)