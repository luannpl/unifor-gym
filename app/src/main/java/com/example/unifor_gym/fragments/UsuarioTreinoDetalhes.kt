package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.unifor_gym.R
import com.example.unifor_gym.models.UsuarioExercicioTreino

class UsuarioTreinoDetalhes : Fragment() {

    private lateinit var txtTituloGrupoMuscular: TextView
    private lateinit var txtTituloExercicioDetalhes: TextView
    private lateinit var txtSubtituloExercicioDetalhes: TextView
    private lateinit var btnVoltarTreinoDetalhes: ImageButton

    // Supino Reto
    private lateinit var layoutEquipamentosSupinoReto: LinearLayout
    private lateinit var layoutDemonstracaoSupinoReto: FrameLayout
    private lateinit var btnDemonstracaoSupinoReto: Button
    private lateinit var btnEquipamentosSupinoReto: Button

    // Supino Inclinado
    private lateinit var layoutEquipamentosSupinoInclinado: LinearLayout
    private lateinit var layoutDemonstracaoSupinoInclinado: FrameLayout
    private lateinit var btnDemonstracaoSupinoInclinado: Button
    private lateinit var btnEquipamentosSupinoInclinado: Button

    private var grupoMuscular: String? = null
    private var exercicioId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usuario_treino_detalhes, container, false)

        // Cabeçalho
        txtTituloGrupoMuscular = view.findViewById(R.id.txtTituloGrupoMuscular)
        txtTituloExercicioDetalhes = view.findViewById(R.id.txtTituloExercicioDetalhes)
        txtSubtituloExercicioDetalhes = view.findViewById(R.id.txtSubtituloExercicioDetalhes)
        btnVoltarTreinoDetalhes = view.findViewById(R.id.btnVoltarTreinoDetalhes)

        // Supino Reto
        layoutEquipamentosSupinoReto = view.findViewById(R.id.layoutEquipamentosSupinoReto)
        layoutDemonstracaoSupinoReto = view.findViewById(R.id.layoutDemonstracaoSupinoReto)
        btnDemonstracaoSupinoReto = view.findViewById(R.id.btnDemonstracaoSupinoReto)
        btnEquipamentosSupinoReto = view.findViewById(R.id.btnEquipamentosSupinoReto)

        // Supino Inclinado
        layoutEquipamentosSupinoInclinado = view.findViewById(R.id.layoutEquipamentosSupinoInclinado)
        layoutDemonstracaoSupinoInclinado = view.findViewById(R.id.layoutDemonstracaoSupinoInclinado)
        btnDemonstracaoSupinoInclinado = view.findViewById(R.id.btnDemonstracaoSupinoInclinado)
        btnEquipamentosSupinoInclinado = view.findViewById(R.id.btnEquipamentosSupinoInclinado)

        // Obter argumentos
        arguments?.let {
            grupoMuscular = it.getString("grupoMuscular")
            exercicioId = it.getInt("exercicioId", -1)
        }

        setupViews()
        setupListeners()

        return view
    }

    private fun setupViews() {
        // Configurar título do grupo muscular
        txtTituloGrupoMuscular.text = grupoMuscular?.let { "← $it" } ?: "← Peito"

        // Configuração inicial do Supino Reto (mostrando equipamentos)
        layoutEquipamentosSupinoReto.visibility = View.VISIBLE
        layoutDemonstracaoSupinoReto.visibility = View.GONE
        btnDemonstracaoSupinoReto.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
        btnDemonstracaoSupinoReto.setTextColor(resources.getColor(android.R.color.black, null))
        btnEquipamentosSupinoReto.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, null))
        btnEquipamentosSupinoReto.setTextColor(resources.getColor(android.R.color.white, null))

        // Configuração inicial do Supino Inclinado (mostrando demonstração)
        layoutEquipamentosSupinoInclinado.visibility = View.GONE
        layoutDemonstracaoSupinoInclinado.visibility = View.VISIBLE
        btnDemonstracaoSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
        btnDemonstracaoSupinoInclinado.setTextColor(resources.getColor(android.R.color.white, null))
        btnEquipamentosSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
        btnEquipamentosSupinoInclinado.setTextColor(resources.getColor(android.R.color.black, null))
    }

    private fun setupListeners() {
        // Botão voltar
        btnVoltarTreinoDetalhes.setOnClickListener {
            requireActivity().onBackPressed()
        }

        // Botões do Supino Reto
        btnDemonstracaoSupinoReto.setOnClickListener {
            // Mostrar demonstração, esconder equipamentos
            layoutDemonstracaoSupinoReto.visibility = View.VISIBLE
            layoutEquipamentosSupinoReto.visibility = View.GONE

            // Atualizar estilo dos botões
            btnDemonstracaoSupinoReto.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
            btnDemonstracaoSupinoReto.setTextColor(resources.getColor(android.R.color.white, null))
            btnEquipamentosSupinoReto.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
            btnEquipamentosSupinoReto.setTextColor(resources.getColor(android.R.color.black, null))
        }

        btnEquipamentosSupinoReto.setOnClickListener {
            // Mostrar equipamentos, esconder demonstração
            layoutEquipamentosSupinoReto.visibility = View.VISIBLE
            layoutDemonstracaoSupinoReto.visibility = View.GONE

            // Atualizar estilo dos botões
            btnEquipamentosSupinoReto.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, null))
            btnEquipamentosSupinoReto.setTextColor(resources.getColor(android.R.color.white, null))
            btnDemonstracaoSupinoReto.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
            btnDemonstracaoSupinoReto.setTextColor(resources.getColor(android.R.color.black, null))
        }

        // Botões do Supino Inclinado
        btnDemonstracaoSupinoInclinado.setOnClickListener {
            // Mostrar demonstração, esconder equipamentos
            layoutDemonstracaoSupinoInclinado.visibility = View.VISIBLE
            layoutEquipamentosSupinoInclinado.visibility = View.GONE

            // Atualizar estilo dos botões
            btnDemonstracaoSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
            btnDemonstracaoSupinoInclinado.setTextColor(resources.getColor(android.R.color.white, null))
            btnEquipamentosSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
            btnEquipamentosSupinoInclinado.setTextColor(resources.getColor(android.R.color.black, null))
        }

        btnEquipamentosSupinoInclinado.setOnClickListener {
            // Mostrar equipamentos, esconder demonstração
            layoutEquipamentosSupinoInclinado.visibility = View.VISIBLE
            layoutDemonstracaoSupinoInclinado.visibility = View.GONE

            // Atualizar estilo dos botões
            btnEquipamentosSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, null))
            btnEquipamentosSupinoInclinado.setTextColor(resources.getColor(android.R.color.white, null))
            btnDemonstracaoSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
            btnDemonstracaoSupinoInclinado.setTextColor(resources.getColor(android.R.color.black, null))
        }
    }
}