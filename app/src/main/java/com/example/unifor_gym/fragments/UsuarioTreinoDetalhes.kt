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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Exercicio
import com.google.firebase.firestore.FirebaseFirestore

class UsuarioTreinoDetalhes : Fragment() {

    private lateinit var txtTituloGrupoMuscular: TextView
    private lateinit var txtTituloExercicioDetalhes: TextView
    private lateinit var txtSubtituloExercicioDetalhes: TextView
    private lateinit var btnVoltarTreinoDetalhes: ImageButton

    // Layouts e botões supino reto
    private lateinit var layoutEquipamentosSupinoReto: LinearLayout
    private lateinit var layoutDemonstracaoSupinoReto: FrameLayout
    private lateinit var btnDemonstracaoSupinoReto: Button
    private lateinit var btnEquipamentosSupinoReto: Button

    // Layouts e botões supino inclinado
    private lateinit var layoutEquipamentosSupinoInclinado: LinearLayout
    private lateinit var layoutDemonstracaoSupinoInclinado: FrameLayout
    private lateinit var btnDemonstracaoSupinoInclinado: Button
    private lateinit var btnEquipamentosSupinoInclinado: Button

    private val db = FirebaseFirestore.getInstance()
    private var grupoMuscular: String? = null
    private var exercicioId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usuario_treino_detalhes, container, false)

        txtTituloGrupoMuscular = view.findViewById(R.id.txtTituloGrupoMuscular)
        txtTituloExercicioDetalhes = view.findViewById(R.id.txtTituloExercicioDetalhes)
        txtSubtituloExercicioDetalhes = view.findViewById(R.id.txtSubtituloExercicioDetalhes)
        btnVoltarTreinoDetalhes = view.findViewById(R.id.btnVoltarTreinoDetalhes)

        layoutEquipamentosSupinoReto = view.findViewById(R.id.layoutEquipamentosSupinoReto)
        layoutDemonstracaoSupinoReto = view.findViewById(R.id.layoutDemonstracaoSupinoReto)
        btnDemonstracaoSupinoReto = view.findViewById(R.id.btnDemonstracaoSupinoReto)
        btnEquipamentosSupinoReto = view.findViewById(R.id.btnEquipamentosSupinoReto)

        layoutEquipamentosSupinoInclinado = view.findViewById(R.id.layoutEquipamentosSupinoInclinado)
        layoutDemonstracaoSupinoInclinado = view.findViewById(R.id.layoutDemonstracaoSupinoInclinado)
        btnDemonstracaoSupinoInclinado = view.findViewById(R.id.btnDemonstracaoSupinoInclinado)
        btnEquipamentosSupinoInclinado = view.findViewById(R.id.btnEquipamentosSupinoInclinado)

        arguments?.let {
            grupoMuscular = it.getString("grupoMuscular")
            exercicioId = it.getInt("exercicioId", -1)
        }

        carregarDetalhesExercicio()
        setupListeners()

        return view
    }

    private fun carregarDetalhesExercicio() {
        if (exercicioId == -1) {
            txtTituloGrupoMuscular.text = grupoMuscular ?: "Peito"
            return
        }

        db.collectionGroup("exercicios")
            .whereEqualTo("id", exercicioId)
            .get()
            .addOnSuccessListener { docs ->
                if (!docs.isEmpty) {
                    val exercicio = docs.documents[0].toObject(Exercicio::class.java)
                    exercicio?.let {
                        txtTituloGrupoMuscular.text = it.grupoMuscular.ifBlank { grupoMuscular ?: "Peito" }
                        txtTituloExercicioDetalhes.text = it.nome
                        txtSubtituloExercicioDetalhes.text = it.instrucoes

                        // Esconde todos os layouts inicialmente
                        layoutEquipamentosSupinoReto.visibility = View.GONE
                        layoutDemonstracaoSupinoReto.visibility = View.GONE
                        layoutEquipamentosSupinoInclinado.visibility = View.GONE
                        layoutDemonstracaoSupinoInclinado.visibility = View.GONE

                        // Exibe layouts conforme o nome do exercício
                        when (it.nome.trim().lowercase()) {
                            "supino reto" -> {
                                layoutEquipamentosSupinoReto.visibility = View.VISIBLE
                            }
                            "supino inclinado" -> {
                                layoutEquipamentosSupinoInclinado.visibility = View.VISIBLE
                            }
                        }
                    }
                } else {
                    txtTituloGrupoMuscular.text = grupoMuscular ?: "Peito"
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Erro ao carregar os detalhes do exercício.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun setupListeners() {
        btnVoltarTreinoDetalhes.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        // Supino reto: troca entre demonstração e equipamentos
        btnDemonstracaoSupinoReto.setOnClickListener {
            layoutDemonstracaoSupinoReto.visibility = View.VISIBLE
            layoutEquipamentosSupinoReto.visibility = View.GONE
            btnDemonstracaoSupinoReto.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
            btnDemonstracaoSupinoReto.setTextColor(resources.getColor(android.R.color.white, null))
            btnEquipamentosSupinoReto.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
            btnEquipamentosSupinoReto.setTextColor(resources.getColor(android.R.color.black, null))
        }
        btnEquipamentosSupinoReto.setOnClickListener {
            layoutEquipamentosSupinoReto.visibility = View.VISIBLE
            layoutDemonstracaoSupinoReto.visibility = View.GONE
            btnEquipamentosSupinoReto.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, null))
            btnEquipamentosSupinoReto.setTextColor(resources.getColor(android.R.color.white, null))
            btnDemonstracaoSupinoReto.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
            btnDemonstracaoSupinoReto.setTextColor(resources.getColor(android.R.color.black, null))
        }

        // Supino inclinado: troca entre demonstração e equipamentos
        btnDemonstracaoSupinoInclinado.setOnClickListener {
            layoutDemonstracaoSupinoInclinado.visibility = View.VISIBLE
            layoutEquipamentosSupinoInclinado.visibility = View.GONE
            btnDemonstracaoSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.holo_blue_light, null))
            btnDemonstracaoSupinoInclinado.setTextColor(resources.getColor(android.R.color.white, null))
            btnEquipamentosSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
            btnEquipamentosSupinoInclinado.setTextColor(resources.getColor(android.R.color.black, null))
        }
        btnEquipamentosSupinoInclinado.setOnClickListener {
            layoutEquipamentosSupinoInclinado.visibility = View.VISIBLE
            layoutDemonstracaoSupinoInclinado.visibility = View.GONE
            btnEquipamentosSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, null))
            btnEquipamentosSupinoInclinado.setTextColor(resources.getColor(android.R.color.white, null))
            btnDemonstracaoSupinoInclinado.setBackgroundColor(resources.getColor(android.R.color.darker_gray, null))
            btnDemonstracaoSupinoInclinado.setTextColor(resources.getColor(android.R.color.black, null))
        }
    }
}
