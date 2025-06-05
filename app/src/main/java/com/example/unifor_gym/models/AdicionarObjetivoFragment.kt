package com.example.unifor_gym.models

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.PerfilUsuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class AdicionarObjetivoFragment : DialogFragment() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.modal_adicionar_objetivo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNomeObjetivo = view.findViewById<EditText>(R.id.etNomeObjetivo)
        val etValorAtual = view.findViewById<EditText>(R.id.etValorAtual)
        val etMetaDesejada = view.findViewById<EditText>(R.id.etMetaDesejada)
        val btnAdicionarObjetivo = view.findViewById<Button>(R.id.btnAdicionarObjetivo)
        val btnFecharModal = view.findViewById<ImageButton>(R.id.btnFecharModal)

        btnAdicionarObjetivo.setOnClickListener {
            adicionarObjetivo(etNomeObjetivo, etValorAtual, etMetaDesejada)
        }

        btnFecharModal.setOnClickListener {
            dismiss()
        }
    }

    private fun adicionarObjetivo(
        etNomeObjetivo: EditText,
        etValorAtual: EditText,
        etMetaDesejada: EditText
    ) {
        val nomeObjetivo = etNomeObjetivo.text.toString().trim()
        val valorAtualStr = etValorAtual.text.toString().trim()
        val metaDesejadaStr = etMetaDesejada.text.toString().trim()

        // Validação básica
        if (TextUtils.isEmpty(nomeObjetivo)) {
            etNomeObjetivo.error = "Nome do objetivo é obrigatório"
            etNomeObjetivo.requestFocus()
            return
        }

        if (TextUtils.isEmpty(valorAtualStr)) {
            etValorAtual.error = "Valor atual é obrigatório"
            etValorAtual.requestFocus()
            return
        }

        if (TextUtils.isEmpty(metaDesejadaStr)) {
            etMetaDesejada.error = "Meta desejada é obrigatória"
            etMetaDesejada.requestFocus()
            return
        }

        // Validar valores numéricos
        val valorAtual = try {
            valorAtualStr.toDouble()
        } catch (e: NumberFormatException) {
            etValorAtual.error = "Valor inválido"
            etValorAtual.requestFocus()
            return
        }

        val metaDesejada = try {
            metaDesejadaStr.toDouble()
        } catch (e: NumberFormatException) {
            etMetaDesejada.error = "Valor inválido"
            etMetaDesejada.requestFocus()
            return
        }

        // Validações de lógica
        if (valorAtual < 0) {
            etValorAtual.error = "Valor deve ser positivo"
            etValorAtual.requestFocus()
            return
        }

        if (metaDesejada <= 0) {
            etMetaDesejada.error = "Meta deve ser maior que zero"
            etMetaDesejada.requestFocus()
            return
        }

        if (metaDesejada == valorAtual) {
            etMetaDesejada.error = "Meta deve ser diferente do valor atual"
            etMetaDesejada.requestFocus()
            return
        }

        // Salvar no Firebase
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = currentUser.uid

        val objetivo = hashMapOf(
            "nome" to nomeObjetivo,
            "valorAtual" to valorAtual,
            "metaDesejada" to metaDesejada,
            "dataCriacao" to FieldValue.serverTimestamp()
        )

        // Disable button to prevent multiple submissions
        val btnAdicionarObjetivo = view?.findViewById<Button>(R.id.btnAdicionarObjetivo)
        btnAdicionarObjetivo?.isEnabled = false
        btnAdicionarObjetivo?.text = "Salvando..."

        db.collection("users")
            .document(userId)
            .collection("objetivos")
            .add(objetivo)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Objetivo adicionado com sucesso", Toast.LENGTH_SHORT).show()

                // Refresh the parent fragment
                refreshParentFragment()

                dismiss()
            }
            .addOnFailureListener { exception ->
                btnAdicionarObjetivo?.isEnabled = true
                btnAdicionarObjetivo?.text = "Adicionar Objetivo"

                Toast.makeText(
                    requireContext(),
                    "Erro ao adicionar objetivo: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun refreshParentFragment() {
        // Find the parent fragment and refresh it
        val parentFragment = parentFragmentManager.fragments.find { it is PerfilUsuario }
        if (parentFragment is PerfilUsuario) {
            // The onResume method will be called automatically when this dialog dismisses
            // But we can also force a refresh by recreating the fragment or calling a refresh method
        }
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        // The parent fragment's onResume will be called automatically when dialog dismisses
        // This will refresh the goals list
    }
}