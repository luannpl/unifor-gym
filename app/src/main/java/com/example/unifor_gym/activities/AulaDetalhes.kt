package com.example.unifor_gym.activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unifor_gym.R

class AulaDetalhes : AppCompatActivity() {
    lateinit var btnVoltar: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aula_detalhes)
        btnVoltar = findViewById(R.id.btnVoltarAulaDetalhes)

        btnVoltar.setOnClickListener {
            finish()
        }
    }
}