package com.example.unifor_gym.repositories

import com.example.unifor_gym.models.Exercicio
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreRepository {
    private val fb = FirebaseFirestore.getInstance()

    fun getExercicioByNome(nome: String, callback: (Exercicio?) -> Unit) {
        fb.collection("exercicios")
            .whereEqualTo("nome", nome)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val exercicio = documents.first().toObject(Exercicio::class.java)
                    callback(exercicio)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
}