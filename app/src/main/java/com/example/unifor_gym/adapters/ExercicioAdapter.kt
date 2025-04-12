package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.Exercicio

class ExercicioAdapter (
    private val lista: List<Exercicio>,
    private val onMoreClick: (Exercicio) -> Unit
) : RecyclerView.Adapter<ExercicioAdapter.ExercicioViewHolder>() {

    inner class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNome: TextView = itemView.findViewById(R.id.txtNomeExercicio)
        val txtNivel: TextView = itemView.findViewById(R.id.txtNivelExercicio)
        val txtGrupoMuscular: TextView = itemView.findViewById(R.id.txtGrupoMuscularExercicio)
        val btnMore: ImageView = itemView.findViewById(R.id.btnMoreItemExercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        val exercicio = lista[position]
        holder.txtNome.text = exercicio.nome
        holder.txtNivel.text = exercicio.nivel
        holder.txtGrupoMuscular.text = exercicio.grupoMuscular

        holder.btnMore.setOnClickListener {
            onMoreClick(exercicio)
        }
    }

    override fun getItemCount(): Int = lista.size
}