package com.example.unifor_gym.activities

import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unifor_gym.R
import com.example.unifor_gym.utils.FirebaseAuthManager
import com.google.android.material.button.MaterialButton

class RecuperarSenha : AppCompatActivity() {

    private lateinit var firebaseAuthManager: FirebaseAuthManager
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_senha)

        // Inicializa FirebaseAuthManager
        firebaseAuthManager = FirebaseAuthManager()

        // Ajusta padding para barra de status e navegação
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnEnviarLink = findViewById<MaterialButton>(R.id.btnEnviarLink)
        val inputEmail = findViewById<EditText>(R.id.inputEmailRecuperar)

        // Ação do botão voltar
        btnBack.setOnClickListener {
            finish()
        }

        // Ação do botão "Enviar Link"
        btnEnviarLink.setOnClickListener {
            val email = inputEmail.text.toString().trim()

            // Validações
            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu e-mail", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show()
            } else {
                btnEnviarLink.isEnabled = false

                firebaseAuthManager.resetPassword(
                    email = email,
                    onSuccess = {
                        Toast.makeText(this, "Link de recuperação enviado para $email", Toast.LENGTH_LONG).show()
                        finish()
                    },
                    onFailure = { exception ->
                        val errorMessage = exception.message ?: "Erro desconhecido ao enviar link."
                        Toast.makeText(this, "Erro ao enviar link: $errorMessage", Toast.LENGTH_LONG).show()
                        btnEnviarLink.isEnabled = true
                    }
                )
            }
        }
    }
}
