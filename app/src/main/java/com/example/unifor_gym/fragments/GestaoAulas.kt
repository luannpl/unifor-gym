package com.example.unifor_gym.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.activities.AulaDetalhes
import com.example.unifor_gym.adapters.AulaAdapter
import com.example.unifor_gym.models.AcoesMenuMais
import android.app.AlertDialog
import android.os.Parcelable
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Aula (
    var id: String = "",
    var nome: String = "",
    var qtdMatriculados: Int = 0,
    var qtdVagas: Int = 0,
    var diaDaSemana: String = "",
    var horarioInicio: String = "",
    var horarioFim: String = "",
    var equipamentos: List<String> = listOf()
)

class GestaoAulas : Fragment() {
    private lateinit var recyclerAulas: RecyclerView
    private lateinit var btnAdicionarAula: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gestao_aulas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GestaoAulas", "onViewCreated iniciado")
        recyclerAulas = view.findViewById(R.id.recyclerAulas)

        val fb = Firebase.firestore
        Log.d("GestaoAulas", "Firebase Firestore instanciado")
        fb.collection("Aulas").get()
            .addOnSuccessListener { result ->
                val listaDeAulas = result.map {
                    doc ->
                    val aula = doc.toObject(Aula::class.java)
                    aula.id = doc.id
                    aula
                }

                val aulasAdapter = AulaAdapter(listaDeAulas) { aula, acao ->
                    when (acao) {
                        AcoesMenuMais.VER_DETALHES -> {
                            val intentAulaDetalhes = Intent(requireContext(), AulaDetalhes::class.java)
                            intentAulaDetalhes.putExtra("aulaId", aula.id)
                            startActivity(intentAulaDetalhes)
                        }
                        AcoesMenuMais.EDITAR -> {
                            mostrarDialogEditarAula("Editar aula", aula)
                        }
                        AcoesMenuMais.EXCLUIR -> {
                            mostrarDialogExclusao("Confirmar exclusão", aula)
                        }
                    }
                }

                recyclerAulas.layoutManager = LinearLayoutManager(requireContext())
                recyclerAulas.adapter = aulasAdapter
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Erro de conexão", Toast.LENGTH_SHORT).show()
            }


        btnAdicionarAula = view.findViewById(R.id.btnAdicionarAula)

        btnAdicionarAula.setOnClickListener {
            mostrarDialogAdicionarAula("Adicionar aula")
        }
    }

    private fun mostrarDialogAdicionarAula(titulo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_aula, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val textViewTitulo = dialogView.findViewById<TextView>(R.id.aulaDialogTitulo)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseDialog)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnAulaDialogCancelar)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnAulaDialogConfirmar)

        textViewTitulo.text = titulo

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            Log.d("GestaoAulas", "Adicionando aula.")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarDialogEditarAula(titulo: String, aula: Aula) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_aula, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val textViewTitulo = dialogView.findViewById<TextView>(R.id.aulaDialogTitulo)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseDialog)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnAulaDialogCancelar)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnAulaDialogConfirmar)

        textViewTitulo.text = titulo

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            Log.d("GestaoAulas", "Editando aula: ${aula.nome}")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarDialogExclusao(titulo: String, aula: Aula) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirmacao, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Ações dos botões
        val textViewTitulo = dialogView.findViewById<TextView>(R.id.textTituloConfirmDialog)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnConfirmDialogCancelar)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnConfirmDialogConfirmar)

        textViewTitulo.text = titulo

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            Log.d("GestaoAulas", "Excluindo aula: ${aula.nome}")
            dialog.dismiss()
        }

        dialog.show()
    }

}