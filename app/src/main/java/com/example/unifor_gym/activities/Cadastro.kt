package com.example.unifor_gym.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unifor_gym.R
import com.google.android.material.button.MaterialButton

class Cadastro : AppCompatActivity() {

    lateinit var inputNomeCadastro: EditText
    lateinit var inputEmailCadastro: EditText
    lateinit var inputSenhaCadastro: EditText
    lateinit var btnCadastro: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        inputNomeCadastro = findViewById(R.id.nomeCadastroId)
        inputEmailCadastro = findViewById(R.id.emailCadastroId)
        inputSenhaCadastro = findViewById(R.id.senhaCadastroId)
        btnCadastro = findViewById(R.id.btnCadastrar)

        btnCadastro.setOnClickListener {
            val nome = inputNomeCadastro.text.toString()
            val email = inputEmailCadastro.text.toString()
            val senha = inputSenhaCadastro.text.toString()

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show()

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }



    }
    fun irParaLogin(view: View) {

        val intent = Intent(this, Login::class.java)
        startActivity(intent)
         finish()
    }
}