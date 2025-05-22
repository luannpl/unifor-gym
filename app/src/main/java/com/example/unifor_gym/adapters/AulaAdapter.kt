package com.example.unifor_gym.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.Aula
import com.example.unifor_gym.models.AcoesMenuMais

class AulaAdapter (
    private val lista: List<Aula>,
    private val onMoreClick: (Aula, AcoesMenuMais) -> Unit
): RecyclerView.Adapter<AulaAdapter.AulaViewHolder>() {

    inner class AulaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeAula)
        val txtQtdMatriculados: TextView = itemView.findViewById(R.id.txtQtdMatriculados)
        val txtQtdVagas: TextView = itemView.findViewById(R.id.txtQtdVagas)
        val btnMore: ImageView = itemView.findViewById(R.id.btnMoreItemAula)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)

        fun bind(aula: Aula) {
            txtNome.text = aula.nome
            txtQtdMatriculados.text = aula.qtdMatriculados.toString()
            txtQtdVagas.text = aula.qtdVagas.toString()
            progressBar.progress = aula.qtdMatriculados
            progressBar.max = aula.qtdVagas
            Log.d("AulaAdapter", "Binding Aula: ID=${aula.id}, Nome=${aula.nome}, Matriculados=${aula.qtdMatriculados}, Vagas=${aula.qtdVagas}")
            btnMore.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.inflate(R.menu.menu_item_gestao)
                popupMenu.setOnMenuItemClickListener { item ->
                    when(item.itemId) {
                        R.id.menu_item_gestao_detalhes -> {
                            onMoreClick(aula, AcoesMenuMais.VER_DETALHES)
                            true
                        }
                        R.id.menu_item_gestao_editar -> {
                            onMoreClick(aula, AcoesMenuMais.EDITAR)
                            true
                        }
                        R.id.menu_item_gestao_excluir -> {
                            onMoreClick(aula, AcoesMenuMais.EXCLUIR)
                            true
                        }
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aula_gestao, parent, false)
        return AulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AulaViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size
}