package com.example.unifor_gym.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.AcoesMenuMais
import com.example.unifor_gym.models.Exercicio

class ExercicioAdapter(
    private val exercicios: List<Exercicio>,
    private val onAction: (Exercicio, AcoesMenuMais) -> Unit
) : RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeExercicioGestao)
        val txtDificuldade: TextView = itemView.findViewById(R.id.txtDificuldadeExercicioGestao)
        val txtGrupoMuscular: TextView = itemView.findViewById(R.id.txtGrupoMuscularExercicioGestao)
        val btnMenu: ImageView = itemView.findViewById(R.id.btnMenuExercicioGestao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio_gestao, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        val exercicio = exercicios[position]

        // Configurar dados básicos
        holder.txtNome.text = exercicio.nome
        holder.txtGrupoMuscular.text = exercicio.grupoMuscular

        // Configurar dificuldade com cores
        holder.txtDificuldade.text = exercicio.dificuldade
        when (exercicio.dificuldade) {
            "Iniciante" -> holder.txtDificuldade.setTextColor(Color.parseColor("#4CAF50")) // Verde
            "Intermediário" -> holder.txtDificuldade.setTextColor(Color.parseColor("#FF9800")) // Laranja
            "Avançado" -> holder.txtDificuldade.setTextColor(Color.parseColor("#F44336")) // Vermelho
            else -> holder.txtDificuldade.setTextColor(Color.parseColor("#9E9E9E")) // Cinza
        }

        // Configurar menu de opções com ícones
        holder.btnMenu.setOnClickListener { view ->
            showPopupMenu(view, exercicio, holder.itemView.context)
        }
    }

    private fun showPopupMenu(view: View, exercicio: Exercicio, context: Context) {
        val popupMenu = PopupMenu(context, view)
        popupMenu.menuInflater.inflate(R.menu.menu_item_gestao, popupMenu.menu)

        // Mostrar ícones no menu popup (requer API level 23+)
        try {
            val method = PopupMenu::class.java.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
            method.isAccessible = true
            method.invoke(popupMenu, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Configurar cliques do menu
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_gestao_detalhes -> {
                    onAction(exercicio, AcoesMenuMais.VER_DETALHES)
                    true
                }
                R.id.menu_item_gestao_editar -> {
                    onAction(exercicio, AcoesMenuMais.EDITAR)
                    true
                }
                R.id.menu_item_gestao_excluir -> {
                    onAction(exercicio, AcoesMenuMais.EXCLUIR)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun getItemCount(): Int = exercicios.size
}