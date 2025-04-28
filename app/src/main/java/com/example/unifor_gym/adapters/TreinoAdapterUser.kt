package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Treino

class TreinoAdapterUser(
    private val treinos: List<Treino>,
    private val onTreinoClick: (Treino) -> Unit
) : RecyclerView.Adapter<TreinoAdapterUser.TreinoViewHolder>() {

    inner class TreinoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeTreino: TextView = itemView.findViewById(R.id.tvNomeTreino)
        val tvQtdExercicios: TextView = itemView.findViewById(R.id.tvQtdExercicios)

        fun bind(treino: Treino) {
            tvNomeTreino.text = treino.titulo
            tvQtdExercicios.text = "${treino.exercicios.size} exercícios"

            // Configurar o clique no item do treino
            itemView.setOnClickListener {
                onTreinoClick(treino)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreinoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treino_user, parent, false)
        return TreinoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreinoViewHolder, position: Int) {
        holder.bind(treinos[position])
    }

    override fun getItemCount(): Int = treinos.size
}