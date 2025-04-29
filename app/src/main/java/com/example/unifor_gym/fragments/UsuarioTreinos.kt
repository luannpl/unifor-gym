package com.example.unifor_gym.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.UsuarioExercicioTreinoAdapter
import com.example.unifor_gym.models.UsuarioExercicioTreino
import com.example.unifor_gym.models.UsuarioGrupoTreino

class UsuarioTreinos : Fragment(), UsuarioExercicioTreinoAdapter.OnExercicioTreinoListener {

    private lateinit var recyclerPeito: RecyclerView
    private lateinit var recyclerCosta: RecyclerView
    private lateinit var recyclerPerna: RecyclerView

    private val gruposTreino = mutableListOf<UsuarioGrupoTreino>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_usuario_treinos, container, false)

        recyclerPeito = view.findViewById(R.id.recyclerPeito)
        recyclerCosta = view.findViewById(R.id.recyclerCosta)
        recyclerPerna = view.findViewById(R.id.recyclerPerna)

        setupMockData()
        setupRecyclerViews()

        return view
    }

    private fun setupMockData() {
        // Grupo Peito
        val exerciciosPeito = listOf(
            UsuarioExercicioTreino(
                id = 1,
                nome = "Supino reto",
                carga = "20kg",
                repeticoes = "12x",
                grupoMuscular = "Peito",
                equipamentos = listOf("Banco de Supino", "Barra", "2x Anilhas de 10kg"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 2,
                nome = "Supino incl.",
                carga = "25kg",
                repeticoes = "12x",
                grupoMuscular = "Peito",
                equipamentos = listOf("Banco Inclinado", "Barra", "2x Anilhas de 12.5kg"),
                dificuldade = "Médio",
                organizacao = "Organizado",
                urlVideo = "https://example.com/video1.mp4"
            ),
            UsuarioExercicioTreino(
                id = 3,
                nome = "Voador",
                carga = "45kg",
                repeticoes = "12x",
                grupoMuscular = "Peito",
                equipamentos = listOf("Máquina Voador"),
                dificuldade = "Fácil",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 4,
                nome = "Crucifixo",
                carga = "30kg",
                repeticoes = "12x",
                grupoMuscular = "Peito",
                equipamentos = listOf("Banco Reto", "2x Halteres de 15kg"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 5,
                nome = "Flexão",
                carga = "------",
                repeticoes = "40x",
                grupoMuscular = "Peito",
                equipamentos = listOf(),
                dificuldade = "Médio",
                organizacao = "Organizado"
            )
        )

        // Grupo Costa
        val exerciciosCosta = listOf(
            UsuarioExercicioTreino(
                id = 6,
                nome = "Puxada fron.",
                carga = "40kg",
                repeticoes = "15x",
                grupoMuscular = "Costa",
                equipamentos = listOf("Máquina Puxada"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 7,
                nome = "Barra",
                carga = "------",
                repeticoes = "12x",
                grupoMuscular = "Costa",
                equipamentos = listOf("Barra Fixa"),
                dificuldade = "Difícil",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 8,
                nome = "Remada alta",
                carga = "45kg",
                repeticoes = "15x",
                grupoMuscular = "Costa",
                equipamentos = listOf("Máquina de Remada", "Cabo"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 9,
                nome = "Remada baixa",
                carga = "30kg",
                repeticoes = "15x",
                grupoMuscular = "Costa",
                equipamentos = listOf("Cabo", "Banco"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 10,
                nome = "Pulley",
                carga = "50kg",
                repeticoes = "15x",
                grupoMuscular = "Costa",
                equipamentos = listOf("Máquina Pulley"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            )
        )

        // Grupo Perna
        val exerciciosPerna = listOf(
            UsuarioExercicioTreino(
                id = 11,
                nome = "Agachamento",
                carga = "40kg",
                repeticoes = "12x",
                grupoMuscular = "Perna",
                equipamentos = listOf("Barra", "2x Anilhas de 20kg"),
                dificuldade = "Difícil",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 12,
                nome = "Leg press",
                carga = "80kg",
                repeticoes = "12x",
                grupoMuscular = "Perna",
                equipamentos = listOf("Máquina Leg Press"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 13,
                nome = "C. extensora",
                carga = "45kg",
                repeticoes = "12x",
                grupoMuscular = "Perna",
                equipamentos = listOf("Cadeira Extensora"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 14,
                nome = "Stiff",
                carga = "30kg",
                repeticoes = "12x",
                grupoMuscular = "Perna",
                equipamentos = listOf("Barra", "2x Anilhas de 15kg"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            ),
            UsuarioExercicioTreino(
                id = 15,
                nome = "Mesa flexora",
                carga = "50kg",
                repeticoes = "12x",
                grupoMuscular = "Perna",
                equipamentos = listOf("Mesa Flexora"),
                dificuldade = "Médio",
                organizacao = "Organizado"
            )
        )

        gruposTreino.add(UsuarioGrupoTreino("PEITO", exerciciosPeito))
        gruposTreino.add(UsuarioGrupoTreino("COSTA", exerciciosCosta))
        gruposTreino.add(UsuarioGrupoTreino("PERNA", exerciciosPerna))
    }

    private fun setupRecyclerViews() {
        // Configurar RecyclerView para PEITO
        val adapterPeito = UsuarioExercicioTreinoAdapter(gruposTreino[0].exercicios, this)
        recyclerPeito.layoutManager = LinearLayoutManager(requireContext())
        recyclerPeito.adapter = adapterPeito

        // Configurar RecyclerView para COSTA
        val adapterCosta = UsuarioExercicioTreinoAdapter(gruposTreino[1].exercicios, this)
        recyclerCosta.layoutManager = LinearLayoutManager(requireContext())
        recyclerCosta.adapter = adapterCosta

        // Configurar RecyclerView para PERNA
        val adapterPerna = UsuarioExercicioTreinoAdapter(gruposTreino[2].exercicios, this)
        recyclerPerna.layoutManager = LinearLayoutManager(requireContext())
        recyclerPerna.adapter = adapterPerna
    }

    override fun onExercicioClick(exercicio: UsuarioExercicioTreino) {
        // Implementar navegação para detalhes do exercício usando FragmentManager
        val detalhesFragment = UsuarioTreinoDetalhes().apply {
            arguments = Bundle().apply {
                putString("grupoMuscular", exercicio.grupoMuscular)
                putInt("exercicioId", exercicio.id)
            }
        }

        // Usar a transação de fragmentos do FragmentManager
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, detalhesFragment)
        transaction.addToBackStack(null) // Permite voltar ao fragmento anterior
        transaction.commit()
    }


}