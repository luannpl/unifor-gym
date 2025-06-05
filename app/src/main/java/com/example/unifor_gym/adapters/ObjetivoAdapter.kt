package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Objetivo

class ObjetivoAdapter(
    private var objetivos: MutableList<Objetivo>,
    private val onEditClick: (Objetivo) -> Unit,
    private val onDeleteClick: (Objetivo) -> Unit
) : RecyclerView.Adapter<ObjetivoAdapter.ObjetivoViewHolder>() {

    inner class ObjetivoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeObjetivo: TextView = itemView.findViewById(R.id.tvNomeObjetivo)
        val tvValorAtual: TextView = itemView.findViewById(R.id.tvValorAtual)
        val tvMetaDesejada: TextView = itemView.findViewById(R.id.tvMetaDesejada)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBarObjetivo)
        val tvProgresso: TextView = itemView.findViewById(R.id.tvProgresso)
        val btnEditarObjetivo: ImageButton = itemView.findViewById(R.id.btnEditarObjetivo)
        val btnRemoverObjetivo: ImageButton = itemView.findViewById(R.id.btnRemoverObjetivo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjetivoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_objetivo, parent, false)
        return ObjetivoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjetivoViewHolder, position: Int) {
        val objetivo = objetivos[position]

        holder.tvNomeObjetivo.text = objetivo.nome
        holder.tvValorAtual.text = "${objetivo.valorAtual} ${objetivo.unidade}"
        holder.tvMetaDesejada.text = "Meta: ${objetivo.metaDesejada} ${objetivo.unidade}"

        val progresso = objetivo.calcularProgresso().toInt()
        holder.progressBar.progress = progresso
        holder.tvProgresso.text = "${progresso}%"

        holder.btnEditarObjetivo.setOnClickListener {
            onEditClick(objetivo)
        }

        holder.btnRemoverObjetivo.setOnClickListener {
            onDeleteClick(objetivo)
        }
    }

    override fun getItemCount(): Int = objetivos.size

    fun atualizarLista(novosObjetivos: List<Objetivo>) {
        objetivos.clear()
        objetivos.addAll(novosObjetivos)
        notifyDataSetChanged()
    }

    fun removerItem(objetivo: Objetivo) {
        val position = objetivos.indexOf(objetivo)
        if (position != -1) {
            objetivos.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun adicionarItem(objetivo: Objetivo) {
        objetivos.add(0, objetivo)
        notifyItemInserted(0)
    }
}