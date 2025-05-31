package com.example.unifor_gym.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.unifor_gym.models.AcoesMenuMais
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.unifor_gym.models.Aparelho

class GestaoAparelhos : Fragment() {
    private lateinit var recyclerAparelhos: RecyclerView
    private lateinit var btnAdicionarAparelho: Button
    private lateinit var fb: FirebaseFirestore
    private lateinit var edtBuscarAparelho: EditText
    private var aparelhos: List<Aparelho> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gestao_aparelhos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GestaoAparelhos", "onViewCreated iniciado")

        recyclerAparelhos = view.findViewById(R.id.recyclerAparelhos)
        btnAdicionarAparelho = view.findViewById(R.id.btnAdicionarAparelho)
        edtBuscarAparelho = view.findViewById(R.id.edtBuscarAparelho)
        fb = Firebase.firestore

        edtBuscarAparelho.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarAparelhos(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        carregarAparelhos()
        btnAdicionarAparelho.setOnClickListener {
            mostrarDialogAdicionarAparelho("Adicionar aparelho")
        }
    }

    private fun mostrarDialogAdicionarAparelho(titulo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_adicionar_aparelho, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnFecharAdicionarAparelho)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnConfirmarAdicionarAparelho)

        val edtNomeAparelho = dialogView.findViewById<EditText>(R.id.edtNomeAparelho)
        val spinnerTipo = dialogView.findViewById<Spinner>(R.id.spinnerTipoAparelho)
        val spinnerStatus = dialogView.findViewById<Spinner>(R.id.spinnerStatusAparelho)

        configurarSpinners(spinnerTipo, spinnerStatus)

        btnClose.setOnClickListener { dialog.dismiss() }

        btnConfirmar.setOnClickListener {
            val nome = edtNomeAparelho.text.toString().trim()
            val tipo = spinnerTipo.selectedItem?.toString()?.trim() ?: ""
            val status = spinnerStatus.selectedItem?.toString()?.trim() ?: ""

            if (nome.isEmpty() || tipo.isEmpty() || status.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val aparelhoData = hashMapOf(
                "nome" to nome,
                "tipo" to tipo,
                "status" to status,
                "dataCriacao" to FieldValue.serverTimestamp()
            )

            fb.collection("aparelhos")
                .add(aparelhoData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Aparelho adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    carregarAparelhos()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao adicionar aparelho: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }

    private fun mostrarDialogEditarAparelho(titulo: String, aparelho: Aparelho) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_adicionar_aparelho, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnFecharAdicionarAparelho)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnConfirmarAdicionarAparelho)

        val edtNomeAparelho = dialogView.findViewById<EditText>(R.id.edtNomeAparelho)
        val spinnerTipo = dialogView.findViewById<Spinner>(R.id.spinnerTipoAparelho)
        val spinnerStatus = dialogView.findViewById<Spinner>(R.id.spinnerStatusAparelho)

        edtNomeAparelho.setText(aparelho.nome)
        configurarSpinners(spinnerTipo, spinnerStatus)

        // Definir seleção atual nos spinners
        definirSelecaoSpinner(spinnerTipo, aparelho.tipo)
        definirSelecaoSpinner(spinnerStatus, aparelho.status)

        btnClose.setOnClickListener { dialog.dismiss() }

        btnConfirmar.setOnClickListener {
            val nome = edtNomeAparelho.text.toString().trim()
            val tipo = spinnerTipo.selectedItem?.toString()?.trim() ?: ""
            val status = spinnerStatus.selectedItem?.toString()?.trim() ?: ""

            if (nome.isEmpty() || tipo.isEmpty() || status.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val aparelhoData = hashMapOf(
                "nome" to nome,
                "tipo" to tipo,
                "status" to status,
                "dataAtualizacao" to FieldValue.serverTimestamp()
            )

            fb.collection("aparelhos")
                .document(aparelho.id)
                .update(aparelhoData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Aparelho editado com sucesso!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    carregarAparelhos()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao editar aparelho: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }

    private fun mostrarDialogExclusao(titulo: String, aparelho: Aparelho) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirmacao, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val textViewTitulo = dialogView.findViewById<TextView>(R.id.textTituloConfirmDialog)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnConfirmDialogCancelar)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnConfirmDialogConfirmar)

        textViewTitulo.text = titulo

        btnCancelar.setOnClickListener { dialog.dismiss() }

        btnConfirmar.setOnClickListener {
            Log.d("GestaoAparelhos", "Excluindo aparelho: ${aparelho.nome}")

            fb.collection("aparelhos")
                .document(aparelho.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("GestaoAparelhos", "Aparelho excluído com sucesso")
                    Toast.makeText(requireContext(), "Aparelho excluído com sucesso!", Toast.LENGTH_SHORT).show()
                    carregarAparelhos()
                }
                .addOnFailureListener { e ->
                    Log.d("GestaoAparelhos", "Erro ao excluir aparelho: ${e.message}")
                    Toast.makeText(requireContext(), "Erro ao excluir aparelho: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun carregarAparelhos() {
        Log.d("GestaoAparelhos", "carregarAparelhos: Iniciando busca no Firestore...")

        fb.collection("aparelhos").get()
            .addOnSuccessListener { result ->
                Log.d("GestaoAparelhos", "carregarAparelhos: Sucesso na busca do Firestore. Documentos encontrados: ${result.size()}")

                if (result.isEmpty) {
                    Log.w("GestaoAparelhos", "carregarAparelhos: Nenhum aparelho encontrado na coleção 'Aparelhos'.")
                    Toast.makeText(requireContext(), "Nenhum aparelho cadastrado.", Toast.LENGTH_LONG).show()
                }

                val listaDeAparelhos = result.mapNotNull { doc ->
                    val aparelho = doc.toObject(Aparelho::class.java)
                    if (aparelho == null) {
                        Log.e("GestaoAparelhos", "carregarAparelhos: Erro ao mapear documento ${doc.id} para Aparelho.")
                    } else {
                        aparelho.id = doc.id
                        Log.d("GestaoAparelhos", "carregarAparelhos: Mapeado: ID=${aparelho.id}, Nome=${aparelho.nome}")
                    }
                    aparelho
                }

                aparelhos = listaDeAparelhos
                Log.d("GestaoAparelhos", "carregarAparelhos: Lista 'aparelhos' populada com ${aparelhos.size} itens.")

                Log.d("GestaoAparelhos", "carregarAparelhos: Chamando buscarAparelhos() com query atual: '${edtBuscarAparelho.text.toString()}'")
                buscarAparelhos(edtBuscarAparelho.text.toString())
            }
            .addOnFailureListener { e ->
                Log.e("GestaoAparelhos", "carregarAparelhos: ERRO DE CONEXÃO ou PERMISSÃO: ${e.message}", e)
                Toast.makeText(requireContext(), "Erro ao carregar aparelhos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun buscarAparelhos(query: String) {
        Log.d("GestaoAparelhos", "buscarAparelhos: Recebida query: '$query'. Tamanho da lista original 'aparelhos': ${aparelhos.size}")

        val filteredList = if (query.isEmpty()) {
            aparelhos
        } else {
            aparelhos.filter {
                it.nome.contains(query, ignoreCase = true) ||
                        it.tipo.contains(query, ignoreCase = true) ||
                        it.status.contains(query, ignoreCase = true)
            }
        }

        Log.d("GestaoAparelhos", "buscarAparelhos: Tamanho da lista filtrada: ${filteredList.size}")

        val adapter = AparelhoAdapter(filteredList, object : AparelhoAdapter.OnAparelhoClickListener {

            override fun onAparelhoClick(aparelho: Aparelho, position: Int) {
                alterarStatusRapido(aparelho)
            }

        })

        recyclerAparelhos.layoutManager = LinearLayoutManager(requireContext())
        recyclerAparelhos.adapter = adapter
    }


    private fun alterarStatusRapido(aparelho: Aparelho) {
        val novoStatus = if (aparelho.status == "Operacional") "Manutenção" else "Operacional"

        val aparelhoData = hashMapOf(
            "status" to novoStatus,
            "dataAtualizacao" to FieldValue.serverTimestamp()
        )

        fb.collection("aparelhos")
            .document(aparelho.id)
            .update(aparelhoData as Map<String, Any>)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Status alterado para $novoStatus", Toast.LENGTH_SHORT).show()
                carregarAparelhos()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao alterar status: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configurarSpinners(spinnerTipo: Spinner, spinnerStatus: Spinner) {
        // Configurar spinner de tipo
        val tiposAparelho = arrayOf("Cardio", "Muscul.")
        context?.let { ctx ->
            val adapterTipo = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, tiposAparelho)
            adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerTipo.adapter = adapterTipo
        }

        // Configurar spinner de status
        val statusOptions = arrayOf("Operacional", "Manutenção")
        context?.let { ctx ->
            val adapterStatus = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, statusOptions)
            adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerStatus.adapter = adapterStatus
        }
    }

    private fun definirSelecaoSpinner(spinner: Spinner, valor: String) {
        val adapter = spinner.adapter as ArrayAdapter<String>
        val position = adapter.getPosition(valor)
        if (position >= 0) {
            spinner.setSelection(position)
        }
    }
}