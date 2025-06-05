package com.example.unifor_gym.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.unifor_gym.R
import com.example.unifor_gym.activities.ConfiguracoesPerfil
import com.example.unifor_gym.models.AdicionarObjetivoFragment
import com.example.unifor_gym.models.ModalFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PerfilUsuario : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var textNomeUsuario: TextView
    private lateinit var tvPeso: TextView
    private lateinit var tvAltura: TextView
    private lateinit var tvIMC: TextView
    private lateinit var tvGC: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false)

        // Inicializar os TextViews
        textNomeUsuario = view.findViewById(R.id.textNomeUsuario)
        tvPeso = view.findViewById(R.id.tvPeso)
        tvAltura = view.findViewById(R.id.tvAltura)
        tvIMC = view.findViewById(R.id.tvIMC)
        tvGC = view.findViewById(R.id.tvGC)

        // Carregar dados do usuário
        carregarDadosUsuario()

        // Botão de configurações
        val btnConfig = view.findViewById<ImageButton>(R.id.btnConfig)
        btnConfig.setOnClickListener {
            // Navega para a atividade ConfiguracoesPerfil
            val intent = Intent(activity, ConfiguracoesPerfil::class.java)
            startActivity(intent)
        }

        // Botão de editar perfil
        val btnEditar = view.findViewById<ImageView>(R.id.btnEditar)
        btnEditar.setOnClickListener {
            val modalFragment = ModalFragment()
            modalFragment.show(parentFragmentManager, "modalEditarPerfil")
        }

        // Botão de adicionar objetivo
        val btnAdicionarObjetivo = view.findViewById<ImageButton>(R.id.btnAdicionarObjetivo)
        btnAdicionarObjetivo.setOnClickListener {
            // Cria uma nova instância do fragmento de adicionar objetivo
            val modalFragment = AdicionarObjetivoFragment()

            // Exibe o modal
            modalFragment.show(parentFragmentManager, "modalAdicionarObjetivo")
        }

        return view
    }

    private fun carregarDadosUsuario() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            // Buscar dados do usuário na coleção "users" no campo "name"
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Carregar nome
                        val nomeUsuario = document.getString("name") ?: "Usuário"
                        textNomeUsuario.text = nomeUsuario

                        // Carregar outras informações se existirem
                        val peso = document.getDouble("peso")
                        val altura = document.getDouble("altura")
                        val imc = document.getDouble("imc")
                        val gc = document.getDouble("gc")

                        // Atualizar os campos se os dados existirem
                        peso?.let {
                            tvPeso.text = "${it.toInt()} kg"
                        }

                        altura?.let {
                            // Assumindo que altura está em metros, converter para cm
                            val alturaCm = if (it < 10) (it * 100).toInt() else it.toInt()
                            tvAltura.text = "$alturaCm cm"
                        }

                        imc?.let {
                            tvIMC.text = String.format("%.1f", it)
                        }

                        gc?.let {
                            tvGC.text = "${it.toInt()}%"
                        }

                    } else {
                        // Se não encontrar no Firestore, usar dados do Firebase Auth
                        val nome = currentUser.displayName ?:
                        currentUser.email?.substringBefore("@") ?:
                        "Usuário"
                        textNomeUsuario.text = nome
                    }
                }
                .addOnFailureListener { exception ->
                    // Em caso de erro, usar dados do Firebase Auth ou padrão
                    val nome = currentUser.displayName ?:
                    currentUser.email?.substringBefore("@") ?:
                    "Usuário"
                    textNomeUsuario.text = nome

                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar dados do perfil",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Log do erro (opcional)
                    println("Erro ao carregar dados do usuário: ${exception.message}")
                }
        } else {
            // Usuário não está logado
            textNomeUsuario.text = "Usuário"
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarregar dados quando o fragment voltar a ficar visível
        carregarDadosUsuario()
    }
}