package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.ExercicioTreino

class ExercicioAdapterUser(
    private val exercicios: List<ExercicioTreino>,
    private val onItemClick: (ExercicioTreino) -> Unit
) : RecyclerView.Adapter<ExercicioAdapterUser.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNomeExercicio)
        val tvPeso: TextView = itemView.findViewById(R.id.tvPesoExercicio)
        val tvReps: TextView = itemView.findViewById(R.id.tvRepsExercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio_user, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        val exercicio = exercicios[position]

        holder.tvNome.text = exercicio.nome
        holder.tvPeso.text = exercicio.peso
        holder.tvReps.text = exercicio.repeticoes

        if (position % 2 == 1) {
            holder.itemView.setBackgroundResource(R.color.light_gray)
        } else {
            holder.itemView.setBackgroundResource(android.R.color.white)
        }

        holder.itemView.setOnClickListener {
            onItemClick(exercicio)
        }
    }

    override fun getItemCount(): Int = exercicios.size
}
