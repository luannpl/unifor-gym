package com.example.unifor_gym.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.AparelhoAdapter
import com.example.unifor_gym.models.Aparelho

class GestaoAparelhos : Fragment(), AparelhoAdapter.OnAparelhoClickListener {

    private lateinit var recyclerAparelhos: RecyclerView
    private lateinit var btnAdicionarAparelho: Button
    private lateinit var edtBuscarAparelho: EditText
    private lateinit var aparelhoAdapter: AparelhoAdapter
    private lateinit var listaAparelhos: MutableList<Aparelho>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_gestao_aparelhos, container, false)

        recyclerAparelhos = view.findViewById(R.id.recyclerAparelhos)
        btnAdicionarAparelho = view.findViewById(R.id.btnAdicionarAparelho)
        edtBuscarAparelho = view.findViewById(R.id.edtBuscarAparelho)

        configurarRecyclerView()
        configurarBotoes()

        return view
    }

    private fun configurarRecyclerView() {
        listaAparelhos = gerarMockAparelhos().toMutableList()
        aparelhoAdapter = AparelhoAdapter(listaAparelhos, this)
        recyclerAparelhos.layoutManager = LinearLayoutManager(requireContext())
        recyclerAparelhos.adapter = aparelhoAdapter
    }

    private fun configurarBotoes() {
        btnAdicionarAparelho.setOnClickListener {
            mostrarDialogAdicionarAparelho()
        }
    }

    private fun gerarMockAparelhos(): List<Aparelho> {
        return listOf(
            Aparelho(id = 1, nome = "Esteira", tipo = "Cardio", status = "Operacional"),
            Aparelho(id = 2, nome = "Leg Press", tipo = "Muscul.", status = "Operacional"),
            Aparelho(id = 3, nome = "Cadeira Extensora", tipo = "Muscul.", status = "Manutenção"),
            Aparelho(id = 4, nome = "Bicicleta", tipo = "Cardio", status = "Operacional"),
            Aparelho(id = 5, nome = "Escada", tipo = "Cardio", status = "Manutenção"),
            Aparelho(id = 6, nome = "Hack", tipo = "Muscul.", status = "Operacional"),
            Aparelho(id = 7, nome = "Polia", tipo = "Muscul.", status = "Operacional"),
            Aparelho(id = 8, nome = "Step", tipo = "Cardio", status = "Operacional")
        )
    }

    private fun mostrarDialogAdicionarAparelho() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_adicionar_aparelho)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnFechar = dialog.findViewById<ImageButton>(R.id.btnFecharAdicionarAparelho)
        val edtNomeAparelho = dialog.findViewById<EditText>(R.id.edtNomeAparelho)
        val spinnerTipo = dialog.findViewById<Spinner>(R.id.spinnerTipoAparelho)
        val spinnerStatus = dialog.findViewById<Spinner>(R.id.spinnerStatusAparelho)
        val btnConfirmar = dialog.findViewById<Button>(R.id.btnConfirmarAdicionarAparelho)

        // Configurar spinner de tipo
        val tiposAparelho = arrayOf("Cardio", "Muscul.")
        val adapterTipo = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tiposAparelho)
        spinnerTipo.adapter = adapterTipo

        // Configurar spinner de status
        val statusOptions = arrayOf("Operacional", "Manutenção")
        val adapterStatus = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statusOptions)
        spinnerStatus.adapter = adapterStatus

        btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            val nome = edtNomeAparelho.text.toString()
            val tipo = spinnerTipo.selectedItem.toString()
            val status = spinnerStatus.selectedItem.toString()

            if (nome.isNotEmpty()) {
                val novoAparelho = Aparelho(
                    id = listaAparelhos.size + 1,
                    nome = nome,
                    tipo = tipo,
                    status = status
                )

                listaAparelhos.add(novoAparelho)
                aparelhoAdapter.notifyItemInserted(listaAparelhos.size - 1)
                dialog.dismiss()
                Toast.makeText(requireContext(), "Aparelho adicionado com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Por favor, informe o nome do aparelho", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun mostrarDialogAlterarStatus(aparelho: Aparelho, position: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.menu_status)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val txtStatusOperacional = dialog.findViewById<TextView>(R.id.txtStatusOperacional)
        val txtStatusManutencao = dialog.findViewById<TextView>(R.id.txtStatusManutencao)

        txtStatusOperacional.setOnClickListener {
            aparelho.status = "Operacional"
            aparelhoAdapter.notifyItemChanged(position)
            dialog.dismiss()
            Toast.makeText(requireContext(), "Status alterado para Operacional", Toast.LENGTH_SHORT).show()
        }

        txtStatusManutencao.setOnClickListener {
            aparelho.status = "Manutenção"
            aparelhoAdapter.notifyItemChanged(position)
            dialog.dismiss()
            Toast.makeText(requireContext(), "Status alterado para Manutenção", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    override fun onAparelhoClick(aparelho: Aparelho, position: Int) {
        mostrarDialogAlterarStatus(aparelho, position)
    }
}