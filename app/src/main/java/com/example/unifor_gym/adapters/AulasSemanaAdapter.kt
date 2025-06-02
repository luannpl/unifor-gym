package com.example.unifor_gym.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.Aula
import com.google.firebase.auth.FirebaseAuth

class AulasSemanaAdapter(
    private var aulas: List<Aula>,
    private val onButtonClick: (aula: Aula, estaMatriculado: Boolean) -> Unit
): RecyclerView.Adapter<AulasSemanaAdapter.AulasSemanaViewHolder>() {
    class AulasSemanaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNomeAula)
        val tvHorario: TextView = itemView.findViewById(R.id.tvHorario)
        val tvInstrutor: TextView = itemView.findViewById(R.id.tvInstrutor)
        val tvVagas: TextView = itemView.findViewById(R.id.tvVagas)
        val btnAcao: Button = itemView.findViewById(R.id.btnAcaoInscricao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AulasSemanaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_card_aula, parent, false)
        return AulasSemanaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AulasSemanaViewHolder, position: Int) {
        val aula = aulas[position]
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val estaMatriculado = aula.alunosMatriculados.contains(userId)

        holder.tvNome.text = aula.nome
        holder.tvHorario.text = "${aula.horarioInicio} - ${aula.horarioFim}"
        holder.tvInstrutor.text = aula.instrutor
        holder.tvVagas.text = "${aula.qtdVagas - aula.qtdMatriculados}"

        if (estaMatriculado) {
            holder.btnAcao.text = "Cancelar Inscrição"
            holder.btnAcao.isEnabled = true
            holder.btnAcao.setBackgroundColor(Color.RED)   // Pode ser vermelho para indicar cancelamento
            holder.btnAcao.setTextColor(Color.WHITE)
        } else {
            if (aula.qtdVagas - aula.qtdMatriculados > 0) {
                holder.btnAcao.text = "Inscrever"
                holder.btnAcao.isEnabled = true
                holder.btnAcao.setBackgroundColor(Color.BLACK)
                holder.btnAcao.setTextColor(Color.WHITE)
            } else {
                holder.btnAcao.text = "Indisponível"
                holder.btnAcao.isEnabled = false
                holder.btnAcao.setBackgroundColor(Color.GRAY)
                holder.btnAcao.setTextColor(Color.DKGRAY)
            }
        }

        holder.btnAcao.setOnClickListener {
            onButtonClick(aula, estaMatriculado)
        }
    }

    fun updateList(novasAulas: List<Aula>) {
        aulas = novasAulas
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = aulas.size
}