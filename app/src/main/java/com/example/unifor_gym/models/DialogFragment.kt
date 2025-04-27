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
import android.view.WindowManager

class ModalFragment : DialogFragment() {

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
        val btnSalvarPerfil = view.findViewById<Button>(R.id.btnSalvarPerfil)

        btnFModal.setOnClickListener {
            dismiss()
        }

        btnSalvarPerfil.setOnClickListener {
            val nomeUsuario = etNomeUsuario.text.toString()
            val peso = etPeso.text.toString()
            val altura = etAltura.text.toString()

            if (TextUtils.isEmpty(nomeUsuario) || TextUtils.isEmpty(peso) || TextUtils.isEmpty(altura)) {
                Toast.makeText(activity, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Perfil atualizado com sucesso", Toast.LENGTH_SHORT).show()
                dismiss()
            }
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
