package com.example.unifor_gym.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.example.unifor_gym.R
import com.example.unifor_gym.activities.ConfiguracoesPerfil
import com.example.unifor_gym.models.AdicionarObjetivoFragment
import com.example.unifor_gym.models.ModalFragment

class PerfilUsuario : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_perfil_usuario, container, false)

        // Botão de configurações
        val btnConfig = view.findViewById<ImageButton>(R.id.btnConfig)
        btnConfig.setOnClickListener {
            // Navega para a atividade ConfiguracoesPerfil
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
            // Cria uma nova instância do fragmento de adicionar objetivo
            val modalFragment = AdicionarObjetivoFragment()

            // Exibe o modal
            modalFragment.show(parentFragmentManager, "modalAdicionarObjetivo")
        }

        return view
    }
}
