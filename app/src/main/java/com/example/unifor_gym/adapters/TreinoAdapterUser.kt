package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Exercicio
import com.example.unifor_gym.models.Treino

class TreinoAdapterUser(
    private val treinos: List<Treino>,
    private val onTreinoClick: ((Treino) -> Unit)? = null // Adicionar callback de clique
) : RecyclerView.Adapter<TreinoAdapterUser.TreinoViewHolder>() {

    inner class TreinoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTituloTreino)
        val rvExercicios: RecyclerView = itemView.findViewById(R.id.recyclerViewExercicios)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TreinoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_treino_user, parent, false)
        return TreinoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TreinoViewHolder, position: Int) {
        val treino = treinos[position]
        holder.tvTitulo.text = treino.titulo

        holder.rvExercicios.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.rvExercicios.adapter = ExercicioAdapterUser(treino.exercicios)

        // Configurando o listener de clique para todo o item
        holder.itemView.setOnClickListener {
            onTreinoClick?.invoke(treino)
        }
    }

    override fun getItemCount(): Int = treinos.size
}