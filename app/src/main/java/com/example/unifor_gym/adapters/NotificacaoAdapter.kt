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

class NotificacaoAdapter(
    private var lista: List<Notificacao>
) : RecyclerView.Adapter<NotificacaoAdapter.NotificacaoViewHolder>() {

    private var onNotificacaoClick: ((Notificacao) -> Unit)? = null

    fun setOnNotificacaoClickListener(callback: (Notificacao) -> Unit) {
        onNotificacaoClick = callback
    }

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
            "aulas" -> R.drawable.aulas
            "usuario" -> R.drawable.usuario
            "aparelhos" -> R.drawable.aparelhos
            "notificacoes" -> R.drawable.notificacoes
            else -> R.drawable.notificacoes
        }
        holder.icone.setImageResource(iconeResId)

        // Todas as notificações com fundo cinza claro
        holder.cardNotificacao.setCardBackgroundColor(
            ContextCompat.getColor(holder.itemView.context, R.color.light_blue_gray)
        )

        // Texto normal para todas as notificações
        holder.titulo.alpha = 1.0f
        holder.descricao.alpha = 1.0f
        holder.tempo.alpha = 1.0f

        // Configurar clique na notificação (se callback estiver definido)
        holder.cardNotificacao.setOnClickListener {
            onNotificacaoClick?.invoke(notificacao)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun atualizarLista(novaLista: List<Notificacao>) {
        lista = novaLista
        notifyDataSetChanged()
    }
}