package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.Aula

class AulaAdapter (
    private val lista: List<Aula>,
    private val onMoreClick: (Aula) -> Unit
): RecyclerView.Adapter<AulaAdapter.AulaViewHolder>() {

    inner class AulaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeAula)
        val txtQtdMatriculados: TextView = itemView.findViewById(R.id.txtQtdMatriculados)
        val txtQtdVagas: TextView = itemView.findViewById(R.id.txtQtdVagas)
        val btnMore: ImageView = itemView.findViewById(R.id.btnMoreItemAula)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aula, parent, false)
        return AulaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AulaViewHolder, position: Int) {
        val aula = lista[position]
        holder.txtNome.text = aula.nome
        holder.txtQtdMatriculados.text = aula.qtdMatriculados
        holder.txtQtdVagas.text = aula.qtdVagas
        holder.progressBar.progress = Integer.parseInt(aula.qtdMatriculados)
        holder.progressBar.max = Integer.parseInt(aula.qtdVagas)

        holder.btnMore.setOnClickListener {
            onMoreClick(aula)
        }
    }

    override fun getItemCount(): Int = lista.size
}