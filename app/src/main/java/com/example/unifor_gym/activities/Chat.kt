package com.example.unifor_gym.activities

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.unifor_gym.R



class Chat : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Encontrar o botão de voltar
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // Configurar o listener para o botão de voltar
        btnBack.setOnClickListener {
            onBackPressed()
        }
    }
}
