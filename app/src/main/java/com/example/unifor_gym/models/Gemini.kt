package com.example.unifor_gym.models

data class GeminiPrompt(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

data class Part(
    val text: String
)

data class GeminiResponse(
    val candidates: List<Candidate>?,
    val promptFeedback: PromptFeedback?
)

data class Candidate(
    val content: ContentResponse?,
    val finishReason: String?,
    val index: Int?,
    val safetyRatings: List<SafetyRating>?
)

data class ContentResponse(
    val parts: List<PartResponse>?,
    val role: String?
)

data class PartResponse(
    val text: String?
)

data class SafetyRating(
    val category: String?,
    val probability: String?
)

data class PromptFeedback(
    val safetyRatings: List<SafetyRating>?
)
