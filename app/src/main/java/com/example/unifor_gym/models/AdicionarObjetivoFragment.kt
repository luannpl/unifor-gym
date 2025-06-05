package com.example.unifor_gym.models

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.unifor_gym.R
import com.example.unifor_gym.repository.ObjetivoRepository
import kotlinx.coroutines.launch

class AdicionarObjetivoFragment : DialogFragment() {

    private val objetivoRepository = ObjetivoRepository()
    private var objetivoParaEditar: Objetivo? = null
    private var onObjetivoAdicionadoListener: ((Objetivo) -> Unit)? = null

    companion object {
        private const val ARG_OBJETIVO = "arg_objetivo"

        fun newInstance(objetivo: Objetivo? = null): AdicionarObjetivoFragment {
            val fragment = AdicionarObjetivoFragment()
            if (objetivo != null) {
                val args = Bundle()
                args.putParcelable(ARG_OBJETIVO, objetivo) // Mudança aqui
                fragment.arguments = args
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            objetivoParaEditar = it.getParcelable(ARG_OBJETIVO) // Mudança aqui
        }
    }

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
        val spinnerUnidade = view.findViewById<Spinner>(R.id.spinnerUnidade)
        val btnAdicionarObjetivo = view.findViewById<Button>(R.id.btnAdicionarObjetivo)
        val btnFecharModal = view.findViewById<ImageButton>(R.id.btnFecharModal)

        // Configura o spinner de unidades
        setupSpinner(spinnerUnidade)

        // Se estiver editando, preenche os campos
        objetivoParaEditar?.let { objetivo ->
            etNomeObjetivo.setText(objetivo.nome)
            etValorAtual.setText(objetivo.valorAtual.toString())
            etMetaDesejada.setText(objetivo.metaDesejada.toString())

            // Seleciona a unidade no spinner
            val adapter = spinnerUnidade.adapter as ArrayAdapter<String>
            val position = adapter.getPosition(objetivo.unidade)
            if (position >= 0) {
                spinnerUnidade.setSelection(position)
            }

            btnAdicionarObjetivo.text = "Atualizar Objetivo"
        }

        btnAdicionarObjetivo.setOnClickListener {
            val nomeObjetivo = etNomeObjetivo.text.toString().trim()
            val valorAtualStr = etValorAtual.text.toString().trim()
            val metaDesejadaStr = etMetaDesejada.text.toString().trim()
            val unidade = spinnerUnidade.selectedItem.toString()

            if (validarCampos(nomeObjetivo, valorAtualStr, metaDesejadaStr)) {
                val valorAtual = valorAtualStr.toDouble()
                val metaDesejada = metaDesejadaStr.toDouble()

                if (objetivoParaEditar != null) {
                    // Atualizar objetivo existente
                    atualizarObjetivo(objetivoParaEditar!!, nomeObjetivo, valorAtual, metaDesejada, unidade)
                } else {
                    // Adicionar novo objetivo
                    adicionarNovoObjetivo(nomeObjetivo, valorAtual, metaDesejada, unidade)
                }
            }
        }

        btnFecharModal.setOnClickListener {
            dismiss()
        }
    }

    private fun setupSpinner(spinner: Spinner) {
        val unidades = arrayOf("kg", "lbs", "cm", "m", "min", "%", "reps", "sets")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, unidades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun validarCampos(nome: String, valorAtual: String, metaDesejada: String): Boolean {
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(valorAtual) || TextUtils.isEmpty(metaDesejada)) {
            Toast.makeText(activity, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            val valor = valorAtual.toDouble()
            val meta = metaDesejada.toDouble()

            if (valor < 0 || meta <= 0) {
                Toast.makeText(activity, "Os valores devem ser positivos", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: NumberFormatException) {
            Toast.makeText(activity, "Digite valores numéricos válidos", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun adicionarNovoObjetivo(nome: String, valorAtual: Double, metaDesejada: Double, unidade: String) {
        val novoObjetivo = Objetivo(
            nome = nome,
            valorAtual = valorAtual,
            metaDesejada = metaDesejada,
            unidade = unidade
        )

        lifecycleScope.launch {
            objetivoRepository.adicionarObjetivo(novoObjetivo)
                .onSuccess { objetivoId ->
                    val objetivoComId = novoObjetivo.copy(id = objetivoId)
                    onObjetivoAdicionadoListener?.invoke(objetivoComId)
                    Toast.makeText(activity, "Objetivo adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .onFailure { exception ->
                    Toast.makeText(
                        activity,
                        "Erro ao adicionar objetivo: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun atualizarObjetivo(
        objetivo: Objetivo,
        nome: String,
        valorAtual: Double,
        metaDesejada: Double,
        unidade: String
    ) {
        val objetivoAtualizado = objetivo.copy(
            nome = nome,
            valorAtual = valorAtual,
            metaDesejada = metaDesejada,
            unidade = unidade
        )

        lifecycleScope.launch {
            objetivoRepository.atualizarObjetivo(objetivoAtualizado)
                .onSuccess {
                    onObjetivoAdicionadoListener?.invoke(objetivoAtualizado)
                    Toast.makeText(activity, "Objetivo atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .onFailure { exception ->
                    Toast.makeText(
                        activity,
                        "Erro ao atualizar objetivo: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    fun setOnObjetivoAdicionadoListener(listener: (Objetivo) -> Unit) {
        onObjetivoAdicionadoListener = listener
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}