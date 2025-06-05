package com.example.unifor_gym.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.unifor_gym.R
import com.example.unifor_gym.activities.Login
import com.example.unifor_gym.models.AdicionarObjetivoFragment
import com.example.unifor_gym.models.EditarPerfilFragment
import com.example.unifor_gym.utils.FirebaseAuthManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.pow

class PerfilUsuario : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var authManager: FirebaseAuthManager

    // Views
    private lateinit var textNomeUsuario: TextView
    private lateinit var tvPeso: TextView
    private lateinit var tvAltura: TextView
    private lateinit var tvIMC: TextView
    private lateinit var tvGC: TextView
    private lateinit var cardMedidas: CardView
    private lateinit var layoutSemMedidas: LinearLayout
    private lateinit var btnInformarMedidas: Button
    private lateinit var containerObjetivos: LinearLayout
    private lateinit var btnAdicionarObjetivo: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false)

        // Initialize FirebaseAuthManager
        authManager = FirebaseAuthManager()

        initializeViews(view)
        setupClickListeners(view)
        carregarDadosUsuario()

        return view
    }

    private fun initializeViews(view: View) {
        textNomeUsuario = view.findViewById(R.id.textNomeUsuario)
        tvPeso = view.findViewById(R.id.tvPeso)
        tvAltura = view.findViewById(R.id.tvAltura)
        tvIMC = view.findViewById(R.id.tvIMC)
        tvGC = view.findViewById(R.id.tvGC)
        cardMedidas = view.findViewById(R.id.cardMedidas)
        layoutSemMedidas = view.findViewById(R.id.layoutSemMedidas)
        btnInformarMedidas = view.findViewById(R.id.btnInformarMedidas)
        containerObjetivos = view.findViewById(R.id.containerObjetivos)
        btnAdicionarObjetivo = view.findViewById(R.id.btnAdicionarObjetivo)
    }

    private fun setupClickListeners(view: View) {
        // Botão de sair (logout) - FIXED: using the correct view parameter
        val btnSair = view.findViewById<ImageButton>(R.id.btnSair)
        btnSair?.setOnClickListener {
            realizarLogout()
        }

        // Botão de editar perfil - FIXED: using the correct view parameter
        val btnEditar = view.findViewById<ImageView>(R.id.btnEditar)
        btnEditar?.setOnClickListener {
            val modalFragment = EditarPerfilFragment()
            modalFragment.show(parentFragmentManager, "modalEditarPerfil")
        }

        // Botão de adicionar objetivo
        btnAdicionarObjetivo.setOnClickListener {
            val modalFragment = AdicionarObjetivoFragment()
            modalFragment.show(parentFragmentManager, "modalAdicionarObjetivo")
        }

        // Botão "Informar suas medidas"
        btnInformarMedidas.setOnClickListener {
            val modalFragment = EditarPerfilFragment()
            modalFragment.show(parentFragmentManager, "modalEditarPerfil")
        }
    }

    private fun carregarDadosUsuario() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Carregar nome
                        val nomeUsuario = document.getString("name") ?: "Usuário"
                        textNomeUsuario.text = nomeUsuario

                        // Verificar se existem medidas
                        val peso = document.getDouble("peso")
                        val altura = document.getDouble("altura") // em metros
                        val gc = document.getDouble("gc")

                        if (peso != null && altura != null) {
                            // Mostrar card de medidas
                            mostrarMedidas(peso, altura, gc)
                        } else {
                            // Mostrar mensagem para informar medidas
                            mostrarMensagemSemMedidas()
                        }

                        // Carregar objetivos
                        carregarObjetivos(userId)

                    } else {
                        val nome = currentUser.displayName ?:
                        currentUser.email?.substringBefore("@") ?:
                        "Usuário"
                        textNomeUsuario.text = nome
                        mostrarMensagemSemMedidas()
                        carregarObjetivos(userId) // Load goals even if user doc doesn't exist
                    }
                }
                .addOnFailureListener { exception ->
                    val nome = currentUser.displayName ?:
                    currentUser.email?.substringBefore("@") ?:
                    "Usuário"
                    textNomeUsuario.text = nome
                    mostrarMensagemSemMedidas()

                    Toast.makeText(
                        requireContext(),
                        "Erro ao carregar dados do perfil",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Still try to load goals
                    carregarObjetivos(currentUser.uid)
                }
        } else {
            textNomeUsuario.text = "Usuário"
            mostrarMensagemSemMedidas()
            mostrarMensagemSemObjetivos()
        }
    }

    private fun mostrarMedidas(peso: Double, altura: Double, gc: Double?) {
        // Ocultar mensagem sem medidas e mostrar card
        layoutSemMedidas.visibility = View.GONE
        cardMedidas.visibility = View.VISIBLE

        // Preencher dados
        tvPeso.text = "${peso.toInt()} kg"

        // Converter altura para cm se necessário
        val alturaCm = if (altura < 10) (altura * 100).toInt() else altura.toInt()
        tvAltura.text = "$alturaCm cm"

        // Calcular IMC
        val alturaMetros = if (altura < 10) altura else altura / 100
        val imc = peso / (alturaMetros.pow(2))
        tvIMC.text = String.format("%.1f", imc)

        // Mostrar GC se disponível
        if (gc != null) {
            tvGC.text = "${gc.toInt()}%"
        } else {
            tvGC.text = "-"
        }
    }

    private fun mostrarMensagemSemMedidas() {
        // Mostrar mensagem e ocultar card
        cardMedidas.visibility = View.GONE
        layoutSemMedidas.visibility = View.VISIBLE
    }

    private fun carregarObjetivos(userId: String) {
        db.collection("users")
            .document(userId)
            .collection("objetivos")
            .get()
            .addOnSuccessListener { result ->
                containerObjetivos.removeAllViews()

                if (result.isEmpty) {
                    // Mostrar mensagem quando não há objetivos
                    mostrarMensagemSemObjetivos()
                } else {
                    // Mostrar objetivos existentes
                    for (document in result) {
                        val nomeObjetivo = document.getString("nome") ?: ""
                        val valorAtual = document.getDouble("valorAtual") ?: 0.0
                        val metaDesejada = document.getDouble("metaDesejada") ?: 1.0

                        adicionarObjetivoView(nomeObjetivo, valorAtual, metaDesejada)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Em caso de erro, mostrar mensagem sem objetivos
                mostrarMensagemSemObjetivos()
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar objetivos: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun mostrarMensagemSemObjetivos() {
        containerObjetivos.removeAllViews()

        val layoutSemObjetivos = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 24)
            gravity = android.view.Gravity.CENTER
        }

        val emojiText = TextView(requireContext()).apply {
            text = "🎯"
            textSize = 32f
            gravity = android.view.Gravity.CENTER
        }

        val mensagemText = TextView(requireContext()).apply {
            text = "Nenhum objetivo cadastrado"
            textSize = 16f
            setTextColor(resources.getColor(R.color.cinza, null))
            gravity = android.view.Gravity.CENTER
        }

        val submensagemText = TextView(requireContext()).apply {
            text = "Adicione seus objetivos para acompanhar seu progresso"
            textSize = 14f
            setTextColor(resources.getColor(R.color.cinza, null))
            gravity = android.view.Gravity.CENTER
            setPadding(0, 8, 0, 0)
        }

        layoutSemObjetivos.addView(emojiText)
        layoutSemObjetivos.addView(mensagemText)
        layoutSemObjetivos.addView(submensagemText)

        containerObjetivos.addView(layoutSemObjetivos)
    }

    private fun adicionarObjetivoView(nome: String, valorAtual: Double, meta: Double) {
        // Create container for this goal
        val goalContainer = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 0)
        }

        // Goal text
        val textView = TextView(requireContext()).apply {
            text = "$nome: ${valorAtual.toInt()} / ${meta.toInt()}"
            textSize = 16f
            setTextColor(resources.getColor(android.R.color.black, null))
        }

        // Progress bar
        val progressBar = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal).apply {
            val progress = if (meta > 0) {
                ((valorAtual / meta) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }
            setProgress(progress)

            // Set color
            progressTintList = resources.getColorStateList(R.color.darkBlue, null)

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                30
            ).apply {
                setMargins(0, 16, 0, 24)
            }
        }

        goalContainer.addView(textView)
        goalContainer.addView(progressBar)
        containerObjetivos.addView(goalContainer)
    }

    private fun realizarLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sair")
            .setMessage("Tem certeza que deseja sair da sua conta?")
            .setPositiveButton("Sair") { _, _ ->
                // Sign out from Firebase
                authManager.signOut()

                Toast.makeText(requireContext(), "Saindo...", Toast.LENGTH_SHORT).show()

                // Navigate to login screen
                val intent = Intent(requireContext(), Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        carregarDadosUsuario()
    }
}