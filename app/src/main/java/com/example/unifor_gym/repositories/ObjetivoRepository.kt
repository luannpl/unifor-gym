package com.example.unifor_gym.repository

import com.example.unifor_gym.models.Objetivo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ObjetivoRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collection = firestore.collection("objetivos")

    // Adiciona um novo objetivo
    suspend fun adicionarObjetivo(objetivo: Objetivo): Result<String> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Usuário não autenticado"))
            val objetivoComUserId = objetivo.copy(userId = userId)

            val documento = collection.add(objetivoComUserId).await()

            // Atualiza o documento com o ID gerado
            val objetivoComId = objetivoComUserId.copy(
                id = documento.id,
                progresso = objetivoComUserId.calcularProgresso()
            )
            collection.document(documento.id).set(objetivoComId).await()

            Result.success(documento.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Busca todos os objetivos do usuário atual
    suspend fun buscarObjetivos(): Result<List<Objetivo>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Usuário não autenticado"))

            val snapshot = collection
                .whereEqualTo("userId", userId)
                .orderBy("dataCriacao", Query.Direction.DESCENDING)
                .get()
                .await()

            val objetivos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Objetivo::class.java)
            }

            Result.success(objetivos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Atualiza um objetivo existente
    suspend fun atualizarObjetivo(objetivo: Objetivo): Result<Unit> {
        return try {
            val objetivoAtualizado = objetivo.copy(progresso = objetivo.calcularProgresso())
            collection.document(objetivo.id).set(objetivoAtualizado).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Remove um objetivo
    suspend fun removerObjetivo(objetivoId: String): Result<Unit> {
        return try {
            collection.document(objetivoId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Atualiza apenas o valor atual de um objetivo
    suspend fun atualizarValorAtual(objetivoId: String, novoValor: Double): Result<Unit> {
        return try {
            val documento = collection.document(objetivoId).get().await()
            val objetivo = documento.toObject(Objetivo::class.java)

            if (objetivo != null) {
                val objetivoAtualizado = objetivo.copy(
                    valorAtual = novoValor,
                    progresso = (novoValor / objetivo.metaDesejada * 100).coerceAtMost(100.0)
                )
                collection.document(objetivoId).set(objetivoAtualizado).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("Objetivo não encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}