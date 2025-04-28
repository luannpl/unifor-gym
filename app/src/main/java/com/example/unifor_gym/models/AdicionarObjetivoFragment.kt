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

class AdicionarObjetivoFragment : DialogFragment() {

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
        val btnFecharModal = view?.findViewById<ImageButton>(R.id.btnFecharModal)

        btnAdicionarObjetivo.setOnClickListener {
            val nomeObjetivo = etNomeObjetivo.text.toString()
            val valorAtual = etValorAtual.text.toString()
            val metaDesejada = etMetaDesejada.text.toString()

            if (TextUtils.isEmpty(nomeObjetivo) || TextUtils.isEmpty(valorAtual) || TextUtils.isEmpty(metaDesejada)) {
                Toast.makeText(activity, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Objetivo adicionado com sucesso", Toast.LENGTH_SHORT).show()

                dismiss()
            }
        }

        btnFecharModal?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }
}
