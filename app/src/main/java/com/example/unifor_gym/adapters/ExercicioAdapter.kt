package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.AcoesMenuMais
import com.example.unifor_gym.models.Exercicio

class ExercicioAdapter(
    private val listaExercicios: List<Exercicio>,
    private val onActionClick: (Exercicio, AcoesMenuMais) -> Unit
) : RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconLetter: TextView = itemView.findViewById(R.id.iconLetterExercicio)
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeExercicio)
        val txtNivel: TextView = itemView.findViewById(R.id.txtNivelExercicio)
        val txtGrupoMuscular: TextView = itemView.findViewById(R.id.txtGrupoMuscularExercicio)
        val btnMore: ImageView = itemView.findViewById(R.id.btnMoreItemExercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio_gestao, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        val exercicio = listaExercicios[position]

        // Configurar a letra do ícone (primeira letra do nome do exercício)
        holder.iconLetter.text = exercicio.nome.first().toString()

        holder.txtNome.text = exercicio.nome
        holder.txtNivel.text = exercicio.dificuldade
        holder.txtGrupoMuscular.text = exercicio.grupoMuscular

        // Configura cores diferentes para os níveis de dificuldade
        when (exercicio.dificuldade.lowercase()) {
            "iniciante" -> {
                holder.txtNivel.setTextColor(holder.itemView.context.getColor(R.color.green))
            }
            "intermediário" -> {
                holder.txtNivel.setTextColor(holder.itemView.context.getColor(R.color.orange))
            }
            "avançado" -> {
                holder.txtNivel.setTextColor(holder.itemView.context.getColor(R.color.red))
            }
        }

        holder.btnMore.setOnClickListener { view ->
            showPopupMenu(view, exercicio)
        }

        holder.itemView.setOnClickListener {
            onActionClick(exercicio, AcoesMenuMais.VER_DETALHES)
        }
    }

    private fun showPopupMenu(view: View, exercicio: Exercicio) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.menu_item_gestao)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_gestao_detalhes -> {
                    onActionClick(exercicio, AcoesMenuMais.VER_DETALHES)
                    true
                }
                R.id.menu_item_gestao_editar -> {
                    onActionClick(exercicio, AcoesMenuMais.EDITAR)
                    true
                }
                R.id.menu_item_gestao_excluir -> {
                    onActionClick(exercicio, AcoesMenuMais.EXCLUIR)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun getItemCount(): Int = listaExercicios.size
}