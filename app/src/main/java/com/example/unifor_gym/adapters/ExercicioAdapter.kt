package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.Exercicio
import com.example.unifor_gym.models.AcoesMenuMais

class ExercicioAdapter (
    private val lista: List<Exercicio>,
    private val onMoreClick: (Exercicio, AcoesMenuMais) -> Unit
) : RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeExercicio)
        val txtNivel: TextView = itemView.findViewById(R.id.txtNivelExercicio)
        val txtGrupoMuscular: TextView = itemView.findViewById(R.id.txtGrupoMuscularExercicio)
        val btnMore: ImageView = itemView.findViewById(R.id.btnMoreItemExercicio)

        fun bind(exercicio: Exercicio) {
            txtNome.text = exercicio.nome
            txtNivel.text = exercicio.nivel
            txtGrupoMuscular.text = exercicio.grupoMuscular

            btnMore.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.inflate(R.menu.menu_item_gestao)
                popupMenu.setOnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.menu_item_gestao_detalhes -> {
                            onMoreClick(exercicio, AcoesMenuMais.VER_DETALHES)
                            true
                        }
                        R.id.menu_item_gestao_editar -> {
                            onMoreClick(exercicio, AcoesMenuMais.EDITAR)
                            true
                        }
                        R.id.menu_item_gestao_excluir -> {
                            onMoreClick(exercicio, AcoesMenuMais.EXCLUIR)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio_gestao, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size
}