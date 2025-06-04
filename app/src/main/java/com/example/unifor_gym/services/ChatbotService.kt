package com.example.unifor_gym.services

import com.example.unifor_gym.models.Content
import com.example.unifor_gym.models.GeminiPrompt
import com.example.unifor_gym.models.Part
import com.example.unifor_gym.network.RetrofitClient
import com.example.unifor_gym.repositories.FirestoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatbotService(
    private val firestoreRepo: FirestoreRepository
) {

    fun responderPergunta(
        pergunta: String,
        coroutineScopeDisponivel: CoroutineScope, // Adicione o CoroutineScope como parâmetro
        callback: (String) -> Unit
    ) {
        if (pergunta.contains("como fazer", true)) {
            val nomeExercicio = pergunta.substringAfter("como fazer").trim()


            firestoreRepo.getExercicioByNome(nomeExercicio) { exercicio ->
                if (exercicio != null) {

                    callback("O exercício ${exercicio.nome} trabalha ${exercicio.grupoMuscular}. Instruções: ${exercicio.instrucoes}")

                } else {
                    chamaGemini(
                        coroutineScopeDisponivel,
                        "Explique como fazer o exercício $nomeExercicio"
                    ) { resposta ->
                        callback(resposta)
                    }
                }
            }
        } else {
            chamaGemini(
                coroutineScopeDisponivel,
                "Responda como um personal trainer de forma concisa: $pergunta, seja breve."
            ) { resposta ->
                callback(resposta)
            }
        }
    }

    private fun chamaGemini(
        scope: CoroutineScope,
        promptText: String,
        callback: (String) -> Unit
    ) {
        val apiKey = RetrofitClient.API_KEY

        if (apiKey == "SUA_CHAVE_AQUI" || apiKey.isBlank()) {
            callback("Erro: Chave da API não configurada.")
            return
        }

        scope.launch(Dispatchers.IO) { // Use o scope fornecido
            try {
                val promptPayload = GeminiPrompt(
                    contents = listOf(
                        Content( // Use a classe Content como definida no artefato
                            parts = listOf(
                                Part(text = promptText) // Use a classe Part como definida
                            )
                        )
                    )
                )

                // Use o nome correto do método: generateContent
                val response = RetrofitClient.api.generateContent(
                    apiKey = apiKey,
                    prompt = promptPayload
                )

                // Acesso mais seguro à resposta
                val textoResposta = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text

                withContext(Dispatchers.Main) {
                    if (textoResposta != null) {
                        callback(textoResposta)
                    } else {
                        // Tente obter mais informações do erro, se disponíveis
                        val errorDetail = response.promptFeedback?.safetyRatings?.joinToString { "${it.category}: ${it.probability}" } ?: "Resposta não encontrada ou formato inesperado."
                        if (response.candidates.isNullOrEmpty()){
                            callback("Erro ao gerar resposta: Nenhum candidato retornado. Detalhes: $errorDetail")
                        } else {
                            callback("Erro ao gerar resposta: $errorDetail")
                        }
                    }
                }
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                withContext(Dispatchers.Main) {
                    callback("Erro HTTP ${e.code()}: ${e.message()}. Detalhes: $errorBody")
                }
            }
            catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback("Erro ao gerar resposta: ${e.localizedMessage}")
                }
            }
        }
    }
}