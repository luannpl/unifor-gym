package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Aparelho

class AparelhoAdapter(
    private val listaAparelhos: List<Aparelho>,
    private val listener: OnAparelhoClickListener
) : RecyclerView.Adapter<AparelhoAdapter.AparelhoViewHolder>() {

    interface OnAparelhoClickListener {
        fun onAparelhoClick(aparelho: Aparelho, position: Int)
    }

    class AparelhoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNome: TextView = view.findViewById(R.id.txtNomeAparelho)
        val txtTipo: TextView = view.findViewById(R.id.txtTipoAparelho)
        val txtStatus: TextView = view.findViewById(R.id.txtStatusAparelho)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AparelhoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aparelho, parent, false)
        return AparelhoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AparelhoViewHolder, position: Int) {
        val aparelho = listaAparelhos[position]

        holder.txtNome.text = aparelho.nome
        holder.txtTipo.text = aparelho.tipo
        holder.txtStatus.text = aparelho.status

        // Definir cor do status
        if (aparelho.status == "Operacional") {
            holder.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
        } else {
            holder.txtStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        }

        // Configurar clique do item
        holder.itemView.setOnClickListener {
            listener.onAparelhoClick(aparelho, position)
        }
    }

    override fun getItemCount() = listaAparelhos.size
}