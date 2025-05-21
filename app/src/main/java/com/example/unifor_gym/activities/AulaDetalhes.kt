package com.example.unifor_gym.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unifor_gym.R

class AulaDetalhes : AppCompatActivity() {
    lateinit var btnVoltar: CardView
    lateinit var txtNome: TextView
    lateinit var txtHorarioInicio: TextView
    lateinit var txtHorarioFim: TextView
    lateinit var txtMatriculados: TextView
    lateinit var txtVagas: TextView
    lateinit var txtEquipamentos: TextView
    lateinit var txtDiaSemana: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aula_detalhes)
        btnVoltar = findViewById(R.id.btnVoltarAulaDetalhes)
        btnVoltar.setOnClickListener {
            finish()
        }

        val aulaId = intent.getStringExtra("aulaId")
        Log.d("AulaDetalhes", "Aula:" + aulaId)
        if(aulaId.isNullOrEmpty()) {
            Toast.makeText(this, "Aula não encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        } else {
            Toast.makeText(this, "Aula encontrada: " + aulaId, Toast.LENGTH_SHORT).show()
        }


    }
}