package com.example.unifor_gym.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.unifor_gym.R

class ConfiguracoesPerfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracoes_perfil)

        // Botão de Voltar
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // Abrir contrato
        val tvContrato = findViewById<TextView>(R.id.tvContrato)
        tvContrato.setOnClickListener {
            Toast.makeText(this, "Baixando contrato...", Toast.LENGTH_SHORT).show()
        }

        // Alterar Senha
        val tvAlterarSenha = findViewById<TextView>(R.id.tvAlterarSenha)
        tvAlterarSenha.setOnClickListener {
            showAlterarSenhaDialog()
        }

        // Sair
        val tvSair = findViewById<TextView>(R.id.tvSair)
        tvSair.setOnClickListener {
            Toast.makeText(this, "Saindo...", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun showAlterarSenhaDialog() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Alterar Senha")
        dialog.setMessage("Funcionalidade em desenvolvimento.")
        dialog.setPositiveButton("OK", null)
        dialog.show()
    }
}
