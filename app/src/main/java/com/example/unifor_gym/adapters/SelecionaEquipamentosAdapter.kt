package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.fragments.Equipamento

class SelecionaEquipamentosAdapter(
    private val equipamentos: List<Equipamento>,
    private val onItemChecked: (Equipamento) -> Unit
) : RecyclerView.Adapter<SelecionaEquipamentosAdapter.EquipamentoViewHolder>() {

    inner class EquipamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxEquipamento)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipamentoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_equipamento_select, parent, false)
        return EquipamentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipamentoViewHolder, position: Int) {
        val equipamento = equipamentos[position]
        holder.checkBox.text = equipamento.nome
        holder.checkBox.isChecked = equipamento.selecionado

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            equipamento.selecionado = isChecked
            onItemChecked(equipamento)
        }
    }

    override fun getItemCount(): Int = equipamentos.size
}
