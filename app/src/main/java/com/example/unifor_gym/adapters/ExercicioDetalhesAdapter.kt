package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Exercicio

class ExercicioDetalhesAdapter(private val exercicios: List<Exercicio>) :
    RecyclerView.Adapter<ExercicioDetalhesAdapter.ExercicioViewHolder>() {

    class ExercicioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNomeExercicio: TextView = itemView.findViewById(R.id.txtNomeExercicio)
        val txtDescricaoExercicio: TextView = itemView.findViewById(R.id.txtDescricaoExercicio)
        val txtEquipamentos: TextView = itemView.findViewById(R.id.txtEquipamentos)
        val btnDemonstracao: Button = itemView.findViewById(R.id.btnDemonstracao)
        val containerVideo: FrameLayout = itemView.findViewById(R.id.containerVideo)

        fun bind(exercicio: Exercicio) {
            txtNomeExercicio.text = exercicio.nome
            txtDescricaoExercicio.text = "Peso: ${exercicio.peso} × ${exercicio.repeticoes}"

            // Exemplo de equipamentos (você precisará adicionar isso ao seu modelo Exercicio)
            if (exercicio.nome.contains("Supino Reto", ignoreCase = true)) {
                txtEquipamentos.text = "- Banco de Supino\n- Barra\n- 2x Anilhas de 10kg"
            } else if (exercicio.nome.contains("Supino Inclinado", ignoreCase = true)) {
                txtEquipamentos.text = "- Banco Inclinado\n- Barra\n- 2x Anilhas de 12,5kg"
            } else {
                txtEquipamentos.text = "- Equipamento específico para ${exercicio.nome}"
            }

            // Configurar botões
            btnDemonstracao.setOnClickListener {
                // Alternar visibilidade do contêiner de vídeo
                if (containerVideo.visibility == View.VISIBLE) {
                    containerVideo.visibility = View.GONE
                } else {
                    containerVideo.visibility = View.VISIBLE
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExercicioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercicio_detalhes, parent, false)
        return ExercicioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExercicioViewHolder, position: Int) {
        holder.bind(exercicios[position])
    }

    override fun getItemCount() = exercicios.size
}