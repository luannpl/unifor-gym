package com.example.unifor_gym.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.unifor_gym.R

class HomeUsuario : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla o layout do fragmento
        val view = inflater.inflate(R.layout.fragment_home_usuario, container, false)

        // Encontra o botão de "Costas"
        val btnVerCostas = view.findViewById<Button>(R.id.btnVerCostas)
        btnVerCostas.setOnClickListener {
            // Substitui o fragmento ao clicar no botão de Costas
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Treinos())  // Substitui o fragmento no contêiner
                .commit()
        }

        // Encontra o botão de "Peito"
        val btnVerPeito = view.findViewById<Button>(R.id.btnVerPeito)
        btnVerPeito.setOnClickListener {
            // Substitui o fragmento ao clicar no botão de Peito
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Treinos())  // Substitui o fragmento no contêiner
                .commit()
        }

        return view
    }
}
