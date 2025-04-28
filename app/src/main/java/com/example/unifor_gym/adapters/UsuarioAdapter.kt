package com.example.unifor_gym.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.models.Usuario

class UsuarioAdapter(
    private val listaUsuarios: List<Usuario>,
    private val listener: OnUsuarioClickListener
) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    interface OnUsuarioClickListener {
        fun onMoreButtonClick(usuario: Usuario, view: View)
    }

    class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtInicial: TextView = view.findViewById(R.id.txtInicialUsuario)
        val txtNome: TextView = view.findViewById(R.id.txtNomeUsuario)
        val txtId: TextView = view.findViewById(R.id.txtIdUsuario)
        val btnMore: ImageButton = view.findViewById(R.id.btnMoreUsuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuarios[position]

        holder.txtInicial.text = usuario.nome
            .split(" ")
            .joinToString("") { it.first().toString().uppercase() }

        holder.txtNome.text = usuario.nome
        holder.txtId.text = usuario.id.toString()
        holder.txtInicial.setBackgroundColor(usuario.corAvatar)

        holder.btnMore.setOnClickListener { view ->
            listener.onMoreButtonClick(usuario, view)
        }
    }

    override fun getItemCount() = listaUsuarios.size
}