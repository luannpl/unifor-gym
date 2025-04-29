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
import com.google.android.material.button.MaterialButton
// import com.google.firebase.auth.FirebaseAuth

class RecuperarSenha : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recuperar_senha)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val btnEnviarLink = findViewById<MaterialButton>(R.id.btnEnviarLink)
        val inputEmail = findViewById<EditText>(R.id.inputEmailRecuperar)

        // Botão de voltar
        btnBack.setOnClickListener {
            finish()
        }

        // Botão de enviar link de recuperação
        btnEnviarLink.setOnClickListener {
            val email = inputEmail.text.toString().trim()

            // Validações de campo
            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu e-mail", Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Digite um e-mail válido", Toast.LENGTH_SHORT).show()
            } else {
                enviarLinkRecuperacao(email)

                // Mensagem simulada
                Toast.makeText(this, "Link de recuperação enviado para $email", Toast.LENGTH_LONG).show()

                finish()
            }
        }
    }

    // Função de envio
    private fun enviarLinkRecuperacao(email: String) {
        /*
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Link enviado para seu e-mail!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro ao enviar link.", Toast.LENGTH_SHORT).show()
                }
            }
        */
    }
}
