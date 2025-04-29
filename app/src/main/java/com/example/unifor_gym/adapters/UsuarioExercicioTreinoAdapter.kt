package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.UsuarioExercicioTreino

class UsuarioExercicioTreinoAdapter(
    private val exercicios: List<UsuarioExercicioTreino>,
    private val listener: OnExercicioTreinoListener
) : RecyclerView.Adapter<UsuarioExercicioTreinoAdapter.ExercicioViewHolder>() {

    interface OnExercicioTreinoListener {
        fun onExercicioClick(exercicio: UsuarioExercicioTreino)
    }

    inner class ExercicioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNome: TextView = view.findViewById(R.id.txtNomeExercicioUsuario)
        val txtCarga: TextView = view.findViewById(R.id.txtCargaExercicioUsuario)
        val txtReps: TextView = view.findViewById(R.id.txtRepsExercicioUsuario)
        val btnInfo: ImageView = view.findViewById(R.id.btnInfoExercicioUsuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario_exercicio_treino, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        val exercicio = exercicios[position]

        holder.txtNome.text = exercicio.nome
        holder.txtCarga.text = exercicio.carga
        holder.txtReps.text = exercicio.repeticoes

        // Configurando o item inteiro para ser clicável
        holder.itemView.setOnClickListener {
            listener.onExercicioClick(exercicio)
        }

        // O botão de info agora é apenas um indicador visual
        // mas ainda pode ser clicado para a mesma função se desejado
        holder.btnInfo.setOnClickListener {
            listener.onExercicioClick(exercicio)
        }
    }

    override fun getItemCount() = exercicios.size
}