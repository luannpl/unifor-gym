package com.example.unifor_gym.network

import com.example.unifor_gym.models.GeminiPrompt
import com.example.unifor_gym.models.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body prompt: GeminiPrompt
    ): GeminiResponse
}