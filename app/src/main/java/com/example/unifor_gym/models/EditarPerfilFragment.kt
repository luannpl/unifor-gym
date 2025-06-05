package com.example.unifor_gym.models

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.unifor_gym.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditarPerfilFragment : DialogFragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.modal_editar_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnFModal = view.findViewById<ImageButton>(R.id.btnFModal)
        val etNomeUsuario = view.findViewById<EditText>(R.id.etNomeUsuario)
        val etPeso = view.findViewById<EditText>(R.id.etPeso)
        val etAltura = view.findViewById<EditText>(R.id.etAltura)
        val etGC = view.findViewById<EditText>(R.id.etGC)
        val btnSalvarPerfil = view.findViewById<Button>(R.id.btnSalvarPerfil)

        // Carregar dados atuais do usuário
        carregarDadosAtuais(etNomeUsuario, etPeso, etAltura, etGC)

        btnFModal.setOnClickListener {
            dismiss()
        }

        btnSalvarPerfil.setOnClickListener {
            salvarPerfil(etNomeUsuario, etPeso, etAltura, etGC)
        }
    }

    private fun carregarDadosAtuais(
        etNomeUsuario: EditText,
        etPeso: EditText,
        etAltura: EditText,
        etGC: EditText
    ) {
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        db.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Carregar nome
                    val nome = document.getString("name") ?: ""
                    etNomeUsuario.setText(nome)

                    // Carregar medidas se existirem
                    val peso = document.getDouble("peso")
                    val altura = document.getDouble("altura")
                    val gc = document.getDouble("gc")

                    peso?.let { etPeso.setText(it.toInt().toString()) }

                    altura?.let {
                        // Converter para cm se necessário
                        val alturaCm = if (it < 10) (it * 100).toInt() else it.toInt()
                        etAltura.setText(alturaCm.toString())
                    }

                    gc?.let { etGC.setText(it.toInt().toString()) }
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
    }

    private fun salvarPerfil(
        etNomeUsuario: EditText,
        etPeso: EditText,
        etAltura: EditText,
        etGC: EditText
    ) {
        val nomeUsuario = etNomeUsuario.text.toString().trim()
        val pesoStr = etPeso.text.toString().trim()
        val alturaStr = etAltura.text.toString().trim()
        val gcStr = etGC.text.toString().trim()

        // Validação básica
        if (TextUtils.isEmpty(nomeUsuario)) {
            etNomeUsuario.error = "Nome é obrigatório"
            return
        }

        if (TextUtils.isEmpty(pesoStr)) {
            etPeso.error = "Peso é obrigatório"
            return
        }

        if (TextUtils.isEmpty(alturaStr)) {
            etAltura.error = "Altura é obrigatória"
            return
        }

        // Validar valores numéricos
        val peso = try {
            pesoStr.toDouble()
        } catch (e: NumberFormatException) {
            etPeso.error = "Valor inválido"
            return
        }

        val altura = try {
            alturaStr.toDouble()
        } catch (e: NumberFormatException) {
            etAltura.error = "Valor inválido"
            return
        }

        val gc = if (gcStr.isNotEmpty()) {
            try {
                gcStr.toDouble()
            } catch (e: NumberFormatException) {
                etGC.error = "Valor inválido"
                return
            }
        } else null

        // Validações de range
        if (peso < 20 || peso > 300) {
            etPeso.error = "Peso deve estar entre 20kg e 300kg"
            return
        }

        if (altura < 100 || altura > 250) {
            etAltura.error = "Altura deve estar entre 100cm e 250cm"
            return
        }

        if (gc != null && (gc < 3 || gc > 50)) {
            etGC.error = "Gordura corporal deve estar entre 3% e 50%"
            return
        }

        // Salvar no Firebase
        val currentUser = auth.currentUser ?: return
        val userId = currentUser.uid

        // Converter altura para metros
        val alturaMetros = altura / 100

        val dadosParaAtualizar = mutableMapOf<String, Any>(
            "name" to nomeUsuario,
            "peso" to peso,
            "altura" to alturaMetros
        )

        // Adicionar GC se foi informado
        gc?.let { dadosParaAtualizar["gc"] = it }

        db.collection("users")
            .document(userId)
            .update(dadosParaAtualizar)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Erro ao atualizar perfil: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
}