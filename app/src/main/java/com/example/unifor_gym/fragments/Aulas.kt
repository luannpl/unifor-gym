package com.example.unifor_gym.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.AulasSemanaAdapter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.unifor_gym.utils.NotificationHelper
import java.text.SimpleDateFormat
import java.util.*

class Aulas : Fragment() {

    private lateinit var fb: FirebaseFirestore
    private val aulas = mutableListOf<Aula>()
    private lateinit var recyclerViewAulas: RecyclerView
    private lateinit var aulasSemanaAdapter: AulasSemanaAdapter
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_aulas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fb = Firebase.firestore
        recyclerViewAulas = view.findViewById(R.id.recyclerViewAulasSemana)
        recyclerViewAulas.layoutManager = LinearLayoutManager(requireContext())

        aulasSemanaAdapter = AulasSemanaAdapter(mutableListOf()) { aula, estaMatriculado ->
            if(estaMatriculado) {
                showCancelInscricaoDialog(aula)
            } else {
                showConfirmInscricaoDialog(aula)
            }
        }
        recyclerViewAulas.adapter = aulasSemanaAdapter

        loadAulas()  // Vai carregar e atualizar o adapter

        setupChipGroup(view)
    }

    private fun loadAulas() {
        fb.collection("Aulas")
            .get()
            .addOnSuccessListener { result ->
                aulas.clear()
                for (document in result) {
                    val aula = document.toObject(Aula::class.java).copy(id = document.id)
                    aulas.add(aula)
                }

                aulasSemanaAdapter.updateList(aulas)
            }
    }

    private fun setupChipGroup(view: View) {
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupDias)
        view.findViewById<Chip>(R.id.chipTodos).isChecked = true

        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                filterClassesByDay(checkedIds[0])
            } else {
                filterClassesByDay(R.id.chipTodos)
            }
        }
    }

    private fun filterClassesByDay(chipId: Int) {
        val diaSelecionado = when (chipId) {
            R.id.chipSeg -> "Segunda"
            R.id.chipTer -> "Terca"
            R.id.chipQua -> "Quarta"
            R.id.chipQui -> "Quinta"
            R.id.chipSex -> "Sexta"
            R.id.chipSab -> "Sabado"
            R.id.chipDom -> "Domingo"
            R.id.chipTodos -> "Todos"
            else -> "Todos"
        }

        val aulasFiltradas = if (diaSelecionado == "Todos") {
            aulas
        } else {
            aulas.filter { it.diaDaSemana == diaSelecionado }
        }

        // Atualiza o adapter com a lista filtrada
        aulasSemanaAdapter.updateList(aulasFiltradas)
    }

    // Dialog de confirmação de inscrição (botão verde/vermelho)
    private fun showConfirmInscricaoDialog(aula: Aula) {
        context?.let { ctx ->
            val dialog = Dialog(ctx)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_confirm_inscricao)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Configurar título do diálogo
            val txtTitulo = dialog.findViewById<TextView>(R.id.txtTituloDialog)
            txtTitulo.text = "Confirmar inscrição?"

            // Botão cancelar (vermelho)
            val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelarInscricao)
            btnCancelar.setOnClickListener {
                dialog.dismiss()
            }

            // Botão confirmar (verde)
            val btnConfirmar = dialog.findViewById<Button>(R.id.btnConfirmarInscricao)
            btnConfirmar.setOnClickListener {
                if(userId.isBlank()) {
                    Toast.makeText(ctx, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val alunosMatriculados = aula.alunosMatriculados.toMutableList()
                alunosMatriculados.add(userId)
                val qtdMatriculados = alunosMatriculados.size
                val dataToUpdate = mapOf(
                    "alunosMatriculados" to alunosMatriculados,
                    "qtdMatriculados" to qtdMatriculados
                )
                fb.collection("Aulas").document(aula.id)
                    .update(dataToUpdate)
                    .addOnSuccessListener {
                        val index = aulas.indexOfFirst { it.id == aula.id }
                        if (index != -1) {
                            // Atualiza a aula na lista local
                            val alunosAtualizados = aula.alunosMatriculados.toMutableList().apply { add(userId) }
                            val aulaAtualizada = aula.copy(
                                alunosMatriculados = alunosAtualizados,
                                qtdMatriculados = alunosAtualizados.size
                            )
                            aulas[index] = aulaAtualizada

                            // Atualiza o adapter com a lista nova
                            aulasSemanaAdapter.updateList(aulas)
                        }

                        // notificação de inscrição em aula
                        NotificationHelper.criarNotificacaoUsuario(
                            userId = userId,
                            titulo = "Inscrição confirmada! ✅",
                            descricao = "Você foi inscrito na aula de ${aula.nome} - ${aula.diaDaSemana} às ${aula.horarioInicio}",
                            tipoIcone = "aulas"
                        )

                        Toast.makeText(requireContext(), "Matriculado com sucesso", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Erro ao se inscrever", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
            }

            dialog.show()
        }
    }

    // Dialog de cancelamento de inscrição
    private fun showCancelInscricaoDialog(aula: Aula) {
        context?.let { ctx ->
            val dialog = Dialog(ctx)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.dialog_cancel_inscricao)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Configurar título do diálogo
            val txtTitulo = dialog.findViewById<TextView>(R.id.txtTituloDialog)
            txtTitulo.text = "Cancelar inscrição?"

            // Botão sair (cinza)
            val btnSair = dialog.findViewById<Button>(R.id.btnSair)
            btnSair.setOnClickListener {
                dialog.dismiss()
            }

            // Botão cancelar (vermelho)
            val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelarInscricao)
            btnCancelar.setOnClickListener {
                if(userId.isBlank()) {
                    Toast.makeText(ctx, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    return@setOnClickListener
                }

                val alunosMatriculados = aula.alunosMatriculados.toMutableList()
                alunosMatriculados.remove(userId)
                val qtdMatriculados = alunosMatriculados.size
                val dataToUpdate = mapOf(
                    "alunosMatriculados" to alunosMatriculados,
                    "qtdMatriculados" to qtdMatriculados
                )
                fb.collection("Aulas").document(aula.id)
                    .update(dataToUpdate)
                    .addOnSuccessListener {
                        val index = aulas.indexOfFirst { it.id == aula.id }
                        if (index != -1) {
                            // Atualiza a aula na lista local
                            val alunosAtualizados = aula.alunosMatriculados.toMutableList().apply { remove(userId) }
                            val aulaAtualizada = aula.copy(
                                alunosMatriculados = alunosAtualizados,
                                qtdMatriculados = alunosAtualizados.size
                            )
                            aulas[index] = aulaAtualizada

                            // Atualiza o adapter com a lista nova
                            aulasSemanaAdapter.updateList(aulas)
                        }

                        // notificação de cancelamento de aula
                        NotificationHelper.criarNotificacaoUsuario(
                            userId = userId,
                            titulo = "Inscrição cancelada 📋",
                            descricao = "Sua inscrição na aula de ${aula.nome} foi cancelada com sucesso",
                            tipoIcone = "aulas"
                        )

                        Toast.makeText(requireContext(), "Matricula cancelada", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Erro ao cancelar matricula", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }

            }

            dialog.show()
        }
    }
}