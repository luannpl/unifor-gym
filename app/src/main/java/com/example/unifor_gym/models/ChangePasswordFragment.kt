package com.example.unifor_gym.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.unifor_gym.R

class ChangePasswordFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.modal_change_password, container, false)

        val etCurrentPassword = view.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = view.findViewById<EditText>(R.id.etConfirmPassword)
        val btnChangePassword = view.findViewById<Button>(R.id.btnChangePassword)
        val tvErrorMessage = view.findViewById<TextView>(R.id.tvErrorMessage)
        val btnXModal = view.findViewById<ImageButton>(R.id.btnXModal)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()

                val isValid = currentPassword.isNotEmpty() &&
                        newPassword.isNotEmpty() &&
                        confirmPassword.isNotEmpty()

                if (isValid) {
                    if (newPassword != confirmPassword) {
                        tvErrorMessage.text = "As senhas não coincidem"
                        tvErrorMessage.visibility = View.VISIBLE
                        btnChangePassword.isEnabled = false
                    } else {
                        tvErrorMessage.visibility = View.GONE
                        btnChangePassword.isEnabled = true
                    }
                } else {
                    tvErrorMessage.visibility = View.GONE
                    btnChangePassword.isEnabled = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etCurrentPassword.addTextChangedListener(textWatcher)
        etNewPassword.addTextChangedListener(textWatcher)
        etConfirmPassword.addTextChangedListener(textWatcher)

        btnChangePassword.setOnClickListener {
            val currentPassword = etCurrentPassword.text.toString()
            val newPassword = etNewPassword.text.toString()

            if (!isCurrentPasswordValid(currentPassword)) {
                tvErrorMessage.text = "Senha atual incorreta"
                tvErrorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (!isNewPasswordValid(newPassword)) {
                tvErrorMessage.text = "A nova senha não atende aos critérios de segurança"
                tvErrorMessage.visibility = View.VISIBLE
                return@setOnClickListener
            }

            showSuccessMessage()
            dismiss() // Fecha o modal
        }

        // Agora sim: configurar o botão X para fechar o modal
        btnXModal.setOnClickListener {
            dismiss() // Apenas fecha o DialogFragment
        }

        return view
    }

    private fun isCurrentPasswordValid(currentPassword: String): Boolean {
        return currentPassword == "senhaAtual123" // Exemplo fixo
    }

    private fun isNewPasswordValid(newPassword: String): Boolean {
        return newPassword.length >= 8 && newPassword.any { it.isUpperCase() }
    }

    private fun showSuccessMessage() {
        Toast.makeText(context, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show()
    }
}
