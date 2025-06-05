package com.example.unifor_gym.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.activities.ConfiguracoesPerfil
import com.example.unifor_gym.adapters.ObjetivoAdapter
import com.example.unifor_gym.models.AdicionarObjetivoFragment
import com.example.unifor_gym.models.ModalFragment
import com.example.unifor_gym.models.Objetivo
import com.example.unifor_gym.repository.ObjetivoRepository
import kotlinx.coroutines.launch

class PerfilUsuario : Fragment() {

    private lateinit var recyclerViewObjetivos: RecyclerView
    private lateinit var objetivoAdapter: ObjetivoAdapter
    private val objetivoRepository = ObjetivoRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false)

        // Configura o RecyclerView
        recyclerViewObjetivos = view.findViewById(R.id.recyclerViewObjetivos)
        setupRecyclerView()

        // Carrega os objetivos
        carregarObjetivos()

        // Botão de configurações
        val btnConfig = view.findViewById<ImageButton>(R.id.btnConfig)
        btnConfig.setOnClickListener {
            val intent = Intent(activity, ConfiguracoesPerfil::class.java)
            startActivity(intent)
        }

        // Botão de editar perfil
        val btnEditar = view.findViewById<ImageView>(R.id.btnEditar)
        btnEditar.setOnClickListener {
            val modalFragment = ModalFragment()
            modalFragment.show(parentFragmentManager, "modalEditarPerfil")
        }

        // Botão de adicionar objetivo
        val btnAdicionarObjetivo = view.findViewById<ImageButton>(R.id.btnAdicionarObjetivo)
        btnAdicionarObjetivo.setOnClickListener {
            val modalFragment = AdicionarObjetivoFragment()
            modalFragment.setOnObjetivoAdicionadoListener { objetivo ->
                objetivoAdapter.adicionarItem(objetivo)
            }
            modalFragment.show(parentFragmentManager, "modalAdicionarObjetivo")
        }

        return view
    }

    private fun setupRecyclerView() {
        objetivoAdapter = ObjetivoAdapter(
            objetivos = mutableListOf(),
            onEditClick = { objetivo ->
                editarObjetivo(objetivo)
            },
            onDeleteClick = { objetivo ->
                removerObjetivo(objetivo)
            }
        )

        recyclerViewObjetivos.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = objetivoAdapter
        }
    }

    private fun carregarObjetivos() {
        lifecycleScope.launch {
            objetivoRepository.buscarObjetivos()
                .onSuccess { objetivos ->
                    objetivoAdapter.atualizarLista(objetivos)
                }
                .onFailure { exception ->
                    Toast.makeText(
                        context,
                        "Erro ao carregar objetivos: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun editarObjetivo(objetivo: Objetivo) {
        val modalFragment = AdicionarObjetivoFragment.newInstance(objetivo)
        modalFragment.setOnObjetivoAdicionadoListener { objetivoAtualizado ->
            carregarObjetivos() // Recarrega a lista
        }
        modalFragment.show(parentFragmentManager, "modalEditarObjetivo")
    }

    private fun removerObjetivo(objetivo: Objetivo) {
        lifecycleScope.launch {
            objetivoRepository.removerObjetivo(objetivo.id)
                .onSuccess {
                    objetivoAdapter.removerItem(objetivo)
                    Toast.makeText(
                        context,
                        "Objetivo removido com sucesso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .onFailure { exception ->
                    Toast.makeText(
                        context,
                        "Erro ao remover objetivo: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    // Método para atualizar a lista quando voltar ao fragment
    override fun onResume() {
        super.onResume()
        carregarObjetivos()
    }
}