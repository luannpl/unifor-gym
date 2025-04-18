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
import com.example.unifor_gym.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class Aulas : Fragment() {

    // Lista para armazenar cards de aulas para facilitar a filtragem
    private val aulaCards = mutableListOf<CardView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_aulas, container, false)

        // Configurar o chip group para selecionar o dia
        setupChipGroup(view)

        // Armazenar referências aos cards de aulas
        setupAulaCards(view)

        // Configurar botões de ação para cada aula
        setupActionButtons(view)

        return view
    }

    private fun setupChipGroup(view: View) {
        val chipGroup = view.findViewById<ChipGroup>(R.id.chipGroupDias)

        // Definir o chip "Sex" como selecionado por padrão (como mostrado na imagem)
        view.findViewById<Chip>(R.id.chipSex).isChecked = true

        chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                filterClassesByDay(checkedIds[0])
            }
        }
    }

    private fun setupAulaCards(view: View) {
        // Adicionar os cards à lista para facilitar a manipulação
        aulaCards.add(view.findViewById(R.id.cardYoga))
        aulaCards.add(view.findViewById(R.id.cardZumba))
        aulaCards.add(view.findViewById(R.id.cardJiuJitsu))
    }

    private fun setupActionButtons(view: View) {
        // Botão inscrever para Yoga
        val btnInscreverYoga = view.findViewById<Button>(R.id.btnInscreverYoga)
        btnInscreverYoga.setOnClickListener {
            showConfirmInscricaoDialog("Yoga")
        }

        // Botão sair para Jiu Jitsu
        val btnSairJiuJitsu = view.findViewById<Button>(R.id.btnSairJiuJitsu)
        btnSairJiuJitsu.setOnClickListener {
            showCancelInscricaoDialog("Jiu Jitsu")
        }
    }

    private fun filterClassesByDay(chipId: Int) {
        // Implementar lógica de filtragem das aulas por dia
        // Exemplo simples: mostrar/esconder cards com base no dia selecionado
        when (chipId) {
            R.id.chipSeg -> {
                // Ex: mostrar apenas aulas de segunda
                aulaCards[0].visibility = View.VISIBLE // Yoga visível na segunda
                aulaCards[1].visibility = View.GONE
                aulaCards[2].visibility = View.GONE
            }
            R.id.chipSex -> {
                // Sexta: mostrar todas as aulas (como na imagem)
                aulaCards.forEach { it.visibility = View.VISIBLE }
            }
            R.id.chipTodos -> {
                aulaCards.forEach { it.visibility = View.VISIBLE }
            }
            else -> {
                // Para outros dias, você pode personalizar conforme necessário
                // Este é apenas um exemplo
                aulaCards[0].visibility = View.GONE
                aulaCards[1].visibility = View.VISIBLE
                aulaCards[2].visibility = View.VISIBLE
            }
        }
    }

    // Dialog de confirmação de inscrição (botão verde/vermelho)
    private fun showConfirmInscricaoDialog(aulaName: String) {
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
                // Realizar a inscrição
                Toast.makeText(ctx, "Inscrito na aula de $aulaName com sucesso!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

                // Atualizar o estado do botão (opcional)
                updateClassStatus(aulaName, true)
            }

            dialog.show()
        }
    }

    // Dialog de cancelamento de inscrição
    private fun showCancelInscricaoDialog(aulaName: String) {
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
                // Cancelar a inscrição
                Toast.makeText(ctx, "Inscrição na aula de $aulaName cancelada!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()

                // Atualizar o estado do botão (opcional)
                updateClassStatus(aulaName, false)
            }

            dialog.show()
        }
    }

    // Atualizar o estado do botão da aula dependendo se está inscrito ou não
    private fun updateClassStatus(aulaName: String, inscrito: Boolean) {
        val view = view ?: return

        when (aulaName) {
            "Yoga" -> {
                val btnYoga = view.findViewById<Button>(R.id.btnInscreverYoga)
                if (inscrito) {
                    btnYoga.text = "Sair"
                    btnYoga.setBackgroundColor(Color.parseColor("#FF6B6B"))
                    // Mudar o listener do botão
                    btnYoga.setOnClickListener {
                        showCancelInscricaoDialog("Yoga")
                    }
                } else {
                    btnYoga.text = "Inscrever"
                    btnYoga.setBackgroundColor(Color.BLACK)
                    btnYoga.setOnClickListener {
                        showConfirmInscricaoDialog("Yoga")
                    }
                }
            }
            "Jiu Jitsu" -> {
                val btnJiuJitsu = view.findViewById<Button>(R.id.btnSairJiuJitsu)
                if (!inscrito) {
                    btnJiuJitsu.text = "Inscrever"
                    btnJiuJitsu.setBackgroundColor(Color.BLACK)
                    btnJiuJitsu.setOnClickListener {
                        showConfirmInscricaoDialog("Jiu Jitsu")
                    }
                }
            }
        }
    }
}