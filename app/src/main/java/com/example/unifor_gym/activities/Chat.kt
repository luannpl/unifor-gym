package com.example.unifor_gym.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.ChatAdapter
import com.example.unifor_gym.models.Message
import com.example.unifor_gym.repositories.FirestoreRepository
import com.example.unifor_gym.services.ChatbotService
import com.example.unifor_gym.utils.VLibrasHelper
import com.google.firebase.FirebaseApp


class Chat : AppCompatActivity() {

    private lateinit var editTextMessage: EditText
    private lateinit var btnEnviar: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatAdapter
    private val chatbotService = ChatbotService(FirestoreRepository())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        FirebaseApp.initializeApp(this)
        editTextMessage = findViewById(R.id.editTextMessage)
        btnEnviar = findViewById(R.id.buttonSend)
        recyclerView = findViewById(R.id.recyclerViewChat)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // Configurar o listener para o botão de voltar
        btnBack.setOnClickListener {
            onBackPressed()
        }

        adapter = ChatAdapter(mutableListOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnEnviar.setOnClickListener {
            val pergunta = editTextMessage.text.toString()
            if (pergunta.isNotEmpty()) {
                enviarPergunta(pergunta)
                editTextMessage.text.clear()
            }
        }

        val btnAccessibility = findViewById<ImageButton>(R.id.btnAccessibility)
        btnAccessibility.setOnClickListener {
            VLibrasHelper.openVLibrasWithScreenContent(this)
        }
    }

    private fun enviarPergunta(pergunta: String) {
        adapter.addMessage(Message(pergunta, true))

        chatbotService.responderPergunta(pergunta, lifecycleScope) { resposta ->
            runOnUiThread {
                adapter.addMessage(Message(resposta, false))
                recyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
}
