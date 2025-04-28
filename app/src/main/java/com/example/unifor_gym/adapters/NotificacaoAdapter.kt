package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Notificacao

class NotificacaoAdapter(private var lista: List<Notificacao>) :
    RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder>() {

    inner class NotificacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardNotificacao: CardView = itemView.findViewById(R.id.cardNotificacao)
        val titulo: TextView = itemView.findViewById(R.id.txtTituloNotificacao)
        val descricao: TextView = itemView.findViewById(R.id.txtDescricaoNotificacao)
        val tempo: TextView = itemView.findViewById(R.id.txtTempoNotificacao)
        val icone: ImageView = itemView.findViewById(R.id.imgIconNotificacao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacao, parent, false)
        return NotificacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificacaoViewHolder, position: Int) {
        val notificacao = lista[position]

        holder.titulo.text = notificacao.titulo
        holder.descricao.text = notificacao.descricao
        holder.tempo.text = notificacao.tempo

        // Define o ícone apropriado
        val iconeResId = when (notificacao.tipoIcone) {
            "exercicios" -> R.drawable.exercicios
            "notificacoes" -> R.drawable.notificacoes
            else -> R.drawable.notificacoes
        }
        holder.icone.setImageResource(iconeResId)

        // Define a cor de fundo baseado no status de leitura
        if (notificacao.lida) {
            holder.cardNotificacao.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
        } else {
            // Cor de fundo para notificações não lidas (cinza claro conforme design)
            holder.cardNotificacao.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.light_blue_gray))
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<Notificacao>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}