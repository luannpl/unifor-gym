package com.example.unifor_gym.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unifor_gym.R
import com.example.unifor_gym.models.UserRole
import com.example.unifor_gym.utils.FirebaseAuthManager
import com.google.android.material.button.MaterialButton

class Login : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var senhaEditText: EditText
    private lateinit var btnEntrar: MaterialButton
    private lateinit var textEsqueceuSenha: TextView
    private lateinit var textCadastrarLogin: TextView

    private lateinit var authManager: FirebaseAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        authManager = FirebaseAuthManager()

        // Check if user is already logged in
        if (authManager.isUserLoggedIn()) {
            checkUserRoleAndNavigate()
            return
        }

        inicializarComponentes()
        configurarListeners()
    }

    private fun inicializarComponentes() {
        emailEditText = findViewById(R.id.emailLoginId)
        senhaEditText = findViewById(R.id.senhaLoginId)
        btnEntrar = findViewById(R.id.btnEntrar)
        textEsqueceuSenha = findViewById(R.id.textEsqueceuSenha)
        textCadastrarLogin = findViewById(R.id.textCadastrarLogin)
    }

    private fun configurarListeners() {
        btnEntrar.setOnClickListener {
            realizarLogin()
        }

        textEsqueceuSenha.setOnClickListener {
            irParaRecuperacaoSenha()
        }

        textCadastrarLogin.setOnClickListener { view ->
            irParaCadastro(view)
        }
    }

    private fun realizarLogin() {
        val email = emailEditText.text.toString().trim()
        val senha = senhaEditText.text.toString().trim()

        // Validar campos
        if (email.isEmpty()) {
            emailEditText.error = "Digite seu email"
            emailEditText.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Digite um email válido"
            emailEditText.requestFocus()
            return
        }

        if (senha.isEmpty()) {
            senhaEditText.error = "Digite sua senha"
            senhaEditText.requestFocus()
            return
        }

        // Show loading state
        btnEntrar.isEnabled = false
        btnEntrar.text = "Entrando..."

        // Perform login with Firebase
        authManager.loginUser(
            email = email,
            password = senha,
            onSuccess = { userProfile ->
                btnEntrar.isEnabled = true
                btnEntrar.text = "Entrar"

                Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

                when (userProfile.role) {
                    UserRole.ADMIN -> irParaAdminActivity()
                    UserRole.USER -> irParaUserActivity()
                }
            },
            onFailure = { exception ->
                btnEntrar.isEnabled = true
                btnEntrar.text = "Entrar"

                val errorMessage = when {
                    exception.message?.contains("badly formatted") == true -> "Email inválido"
                    exception.message?.contains("password is invalid") == true -> "Senha incorreta"
                    exception.message?.contains("no user record") == true -> "Usuário não encontrado"
                    exception.message?.contains("temporarily disabled") == true -> "Conta temporariamente bloqueada"
                    else -> "Erro no login: ${exception.message}"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun checkUserRoleAndNavigate() {
        authManager.getCurrentUserProfile(
            onSuccess = { userProfile ->
                when (userProfile.role) {
                    UserRole.ADMIN -> irParaAdminActivity()
                    UserRole.USER -> irParaUserActivity()
                }
            },
            onFailure = {
                // If profile not found, sign out and stay on login
                authManager.signOut()
                Toast.makeText(this, "Erro ao carregar perfil do usuário", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun irParaRecuperacaoSenha() {
        val intent = Intent(this, RecuperarSenha::class.java)
        startActivity(intent)
    }

    fun irParaCadastro(view: View) {
        val intent = Intent(this, Cadastro::class.java)
        startActivity(intent)
    }

    private fun irParaAdminActivity() {
        val intent = Intent(this, AdminActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun irParaUserActivity() {
        val intent = Intent(this, UserActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}