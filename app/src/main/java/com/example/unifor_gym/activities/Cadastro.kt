package com.example.unifor_gym.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unifor_gym.R
import com.example.unifor_gym.utils.FirebaseAuthManager
import com.google.android.material.button.MaterialButton

class Cadastro : AppCompatActivity() {

    private lateinit var inputNomeCadastro: EditText
    private lateinit var inputEmailCadastro: EditText
    private lateinit var inputSenhaCadastro: EditText
    private lateinit var btnCadastro: MaterialButton

    private lateinit var authManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cadastro)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authManager = FirebaseAuthManager()

        inputNomeCadastro = findViewById(R.id.nomeCadastroId)
        inputEmailCadastro = findViewById(R.id.emailCadastroId)
        inputSenhaCadastro = findViewById(R.id.senhaCadastroId)
        btnCadastro = findViewById(R.id.btnCadastrar)

        btnCadastro.setOnClickListener {
            realizarCadastro()
        }
    }

    private fun realizarCadastro() {
        val nome = inputNomeCadastro.text.toString().trim()
        val email = inputEmailCadastro.text.toString().trim()
        val senha = inputSenhaCadastro.text.toString().trim()

        // Validações
        if (nome.isEmpty()) {
            inputNomeCadastro.error = "Digite seu nome"
            inputNomeCadastro.requestFocus()
            return
        }

        if (email.isEmpty()) {
            inputEmailCadastro.error = "Digite seu email"
            inputEmailCadastro.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmailCadastro.error = "Digite um email válido"
            inputEmailCadastro.requestFocus()
            return
        }

        if (senha.isEmpty()) {
            inputSenhaCadastro.error = "Digite sua senha"
            inputSenhaCadastro.requestFocus()
            return
        }

        if (senha.length < 6) {
            inputSenhaCadastro.error = "A senha deve ter pelo menos 6 caracteres"
            inputSenhaCadastro.requestFocus()
            return
        }

        // Show loading state
        btnCadastro.isEnabled = false
        btnCadastro.text = "Cadastrando..."

        // Perform registration with Firebase
        authManager.registerUser(
            name = nome,
            email = email,
            password = senha,
            onSuccess = { _ ->
                btnCadastro.isEnabled = true
                btnCadastro.text = "Cadastrar"

                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

                // Navigate to login screen
                val intent = Intent(this, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            },
            onFailure = { exception ->
                btnCadastro.isEnabled = true
                btnCadastro.text = "Cadastrar"

                val errorMessage = when {
                    exception.message?.contains("email address is already in use") == true ->
                        "Este email já está cadastrado"
                    exception.message?.contains("password is too weak") == true ->
                        "Senha muito fraca"
                    exception.message?.contains("email address is badly formatted") == true ->
                        "Email inválido"
                    else -> "Erro no cadastro: ${exception.message}"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
    }

    fun irParaLogin(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}