package com.example.unifor_gym.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.activities.AulaDetalhes
import com.example.unifor_gym.adapters.AulaAdapter
import com.example.unifor_gym.models.AcoesMenuMais
import android.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.unifor_gym.adapters.SelecionaEquipamentosAdapter
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class Aula (
    var id: String = "",
    var nome: String = "",
    var instrutor: String = "",
    var qtdMatriculados: Int = 0,
    var qtdVagas: Int = 0,
    var diaDaSemana: String = "",
    var horarioInicio: String = "",
    var horarioFim: String = "",
    var equipamentos: List<String> = listOf(),
    var alunosMatriculados: List<String> = listOf()
)

data class Equipamento(
    val id: String,
    val nome: String,
    var selecionado: Boolean = false
)

class GestaoAulas : Fragment() {
    private lateinit var recyclerAulas: RecyclerView
    private lateinit var btnAdicionarAula: Button
    private lateinit var fb: FirebaseFirestore
    private lateinit var edxBuscarAula: EditText
    private lateinit var timeWatcher: TextWatcher
    private var aulas: List<Aula> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gestao_aulas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GestaoAulas", "onViewCreated iniciado")
        recyclerAulas = view.findViewById(R.id.recyclerAulas)
        btnAdicionarAula = view.findViewById(R.id.btnAdicionarAula)
        edxBuscarAula = view.findViewById(R.id.buscaAulaGestao)
        fb = Firebase.firestore
        edxBuscarAula.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarAulas(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        timeWatcher = object : TextWatcher {
            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return
                isUpdating = true

                val digits = s.toString().replace("[^\\d]".toRegex(), "").take(4)

                var formatted = when (digits.length) {
                    0, 1, 2 -> digits
                    else -> "${digits.substring(0, 2)}:${digits.substring(2)}"
                }

                // Validação se completou os 4 dígitos
                if (digits.length == 4) {
                    val horas = digits.substring(0, 2).toInt()
                    val minutos = digits.substring(2, 4).toInt()

                    val horaValida = horas.coerceIn(0, 23)
                    val minutoValido = minutos.coerceIn(0, 59)

                    formatted = String.format("%02d:%02d", horaValida, minutoValido)
                }

                s?.replace(0, s.length, formatted, 0, formatted.length)

                isUpdating = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        carregarAulas()
        btnAdicionarAula.setOnClickListener {
            mostrarDialogAdicionarAula("Adicionar aula")
        }
    }

    private fun mostrarDialogAdicionarAula(titulo: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_aula, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val listaEquipamentos = mutableListOf<Equipamento>()

        val recyclerEquipamentos = dialogView.findViewById<RecyclerView>(R.id.recyclerSelecionaEquipamento)
        recyclerEquipamentos.layoutManager = LinearLayoutManager(requireContext())

        val equipamentoAdapter = SelecionaEquipamentosAdapter(listaEquipamentos) { equipamento ->
            // Callback do adapter — já atualiza 'selecionado' dentro do adapter, aqui não precisa fazer nada
        }
        recyclerEquipamentos.adapter = equipamentoAdapter

        configurarRecyclerEquipamentos(recyclerEquipamentos, listaEquipamentos, equipamentoAdapter)

        val textViewTitulo = dialogView.findViewById<TextView>(R.id.aulaDialogTitulo)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseDialog)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnAulaDialogCancelar)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnAulaDialogConfirmar)

        val editNomeAula = dialogView.findViewById<EditText>(R.id.editNomeAula)
        val spinnerInstrutor = dialogView.findViewById<Spinner>(R.id.spinnerInstrutor)
        val spinnerDiaSemana = dialogView.findViewById<Spinner>(R.id.spinnerDiaSemana)
        val editHorarioInicio = dialogView.findViewById<EditText>(R.id.editHorarioInicio)
        val editHorarioFim = dialogView.findViewById<EditText>(R.id.editHorarioFim)
        val editVagas = dialogView.findViewById<EditText>(R.id.editVagas)

        textViewTitulo.text = titulo
        configurarSpinners(spinnerInstrutor, spinnerDiaSemana)

        editHorarioInicio.addTextChangedListener(timeWatcher)
        editHorarioFim.addTextChangedListener(timeWatcher)

        btnCancelar.setOnClickListener { dialog.dismiss() }
        btnClose.setOnClickListener { dialog.dismiss() }

        btnConfirmar.setOnClickListener {
            val nome = editNomeAula.text.toString().trim()
            val instrutor = spinnerInstrutor.selectedItem?.toString()?.trim() ?: ""
            val diaDaSemana = spinnerDiaSemana.selectedItem?.toString()?.trim() ?: ""
            val equipamentosSelecionados = listaEquipamentos.filter { it.selecionado }
            val horarioInicio = editHorarioInicio.text.toString().trim()
            val horarioFim = editHorarioFim.text.toString().trim()
            val qtdVagas = editVagas.text.toString().trim().toIntOrNull()

            if (nome.isEmpty() || horarioInicio.isEmpty() || horarioFim.isEmpty() ||
                qtdVagas == null || instrutor.isEmpty() || diaDaSemana.isEmpty() || equipamentosSelecionados.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val aulaData = hashMapOf(
                "nome" to nome,
                "instrutor" to instrutor,
                "diaDaSemana" to diaDaSemana,
                "equipamentos" to equipamentosSelecionados.map { it.id },
                "horarioInicio" to horarioInicio,
                "horarioFim" to horarioFim,
                "qtdVagas" to qtdVagas,
                "qtdMatriculados" to 0,
                "alunosMatriculados" to emptyList<String>(),
                "dataCriacao" to FieldValue.serverTimestamp()
            )

            fb.collection("Aulas")
                .add(aulaData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Aula adicionada com sucesso!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    carregarAulas()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao adicionar aula: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }



    private fun mostrarDialogEditarAula(titulo: String, aula: Aula) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_aula, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // --- INÍCIO: configuração RecyclerView para selecionar equipamentos ---
        val listaEquipamentos = mutableListOf<Equipamento>()
        val recyclerEquipamentos = dialogView.findViewById<RecyclerView>(R.id.recyclerSelecionaEquipamento)
        recyclerEquipamentos.layoutManager = LinearLayoutManager(requireContext())
        val equipamentoAdapter = SelecionaEquipamentosAdapter(listaEquipamentos) { equipamento ->
            // não precisa fazer nada aqui, o estado 'selecionado' já é atualizado no adapter
        }
        recyclerEquipamentos.adapter = equipamentoAdapter

        configurarRecyclerEquipamentos(recyclerEquipamentos, listaEquipamentos, equipamentoAdapter)
        // --- FIM: configuração RecyclerView ---

        val textViewTitulo = dialogView.findViewById<TextView>(R.id.aulaDialogTitulo)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseDialog)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnAulaDialogCancelar)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnAulaDialogConfirmar)

        val editNomeAula = dialogView.findViewById<EditText>(R.id.editNomeAula)
        val spinnerInstrutor = dialogView.findViewById<Spinner>(R.id.spinnerInstrutor)
        val spinnerDiaSemana = dialogView.findViewById<Spinner>(R.id.spinnerDiaSemana)
        val editHorarioInicio = dialogView.findViewById<EditText>(R.id.editHorarioInicio)
        val editHorarioFim = dialogView.findViewById<EditText>(R.id.editHorarioFim)
        val editVagas = dialogView.findViewById<EditText>(R.id.editVagas)

        textViewTitulo.text = titulo
        editNomeAula.setText(aula.nome)
        editHorarioInicio.setText(aula.horarioInicio)
        editHorarioFim.setText(aula.horarioFim)
        editVagas.setText(aula.qtdVagas.toString())
        configurarSpinners(spinnerInstrutor, spinnerDiaSemana)

        editHorarioInicio.addTextChangedListener(timeWatcher)
        editHorarioFim.addTextChangedListener(timeWatcher)

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            val nome = editNomeAula.text.toString().trim()
            val instrutor = spinnerInstrutor.selectedItem?.toString()?.trim() ?: ""
            val diaDaSemana = spinnerDiaSemana.selectedItem?.toString()?.trim() ?: ""
            val equipamentos = listaEquipamentos.filter { it.selecionado }.map { it.id }
            val horarioInicio = editHorarioInicio.text.toString().trim()
            val horarioFim = editHorarioFim.text.toString().trim()
            val qtdVagas = editVagas.text.toString().trim().toIntOrNull()
            val qtdMatriculados = aula.qtdMatriculados

            if (nome.isEmpty() ||
                horarioInicio.isEmpty() ||
                horarioFim.isEmpty() || qtdVagas == null || instrutor.isEmpty() || diaDaSemana.isEmpty() || equipamentos.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos corretamente.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val aulaData = hashMapOf(
                "nome" to nome,
                "instrutor" to instrutor,
                "diaDaSemana" to diaDaSemana,
                "equipamentos" to equipamentos,
                "horarioInicio" to horarioInicio,
                "horarioFim" to horarioFim,
                "qtdVagas" to qtdVagas,
                "qtdMatriculados" to qtdMatriculados,
                "dataCriacao" to FieldValue.serverTimestamp()
            )

            fb.collection("Aulas")
                .document(aula.id)
                .update(aulaData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Aula editada com sucesso!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    carregarAulas()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao editar aula: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }



    private fun mostrarDialogExclusao(titulo: String, aula: Aula) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirmacao, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Ações dos botões
        val textViewTitulo = dialogView.findViewById<TextView>(R.id.textTituloConfirmDialog)
        val btnCancelar = dialogView.findViewById<Button>(R.id.btnConfirmDialogCancelar)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnConfirmDialogConfirmar)

        textViewTitulo.text = titulo

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            Log.d("GestaoAulas", "Excluindo aula: ${aula.nome}")
            val aulaParaExcluir = fb.collection("Aulas").document(aula.id)

            aulaParaExcluir.delete()
                .addOnSuccessListener {
                    Log.d("GestaoAulas", "Aula excluída com sucesso")
                    carregarAulas()
                }
                .addOnFailureListener { e ->
                    Log.d("GestaoAulas", "Erro ao excluir aula: ${e.message}")
                }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun carregarAulas() {
        Log.d("GestaoAulas", "carregarAulas: Iniciando busca no Firestore...")
        fb.collection("Aulas").get()
            .addOnSuccessListener { result ->
                Log.d("GestaoAulas", "carregarAulas: Sucesso na busca do Firestore. Documentos encontrados: ${result.size()}")

                if (result.isEmpty) {
                    Log.w("GestaoAulas", "carregarAulas: Nenhuma aula encontrada na coleção 'Aulas'.")
                    Toast.makeText(requireContext(), "Nenhuma aula cadastrada.", Toast.LENGTH_LONG).show()
                }

                val listaDeAulas = result.mapNotNull { doc -> // Use mapNotNull para lidar com possíveis nulos de toObject
                    val aula = doc.toObject(Aula::class.java)
                    if (aula == null) {
                        Log.e("GestaoAulas", "carregarAulas: Erro ao mapear documento ${doc.id} para Aula. Verifique a data class e campos do Firestore.")
                    } else {
                        aula.id = doc.id // Certifique-se de que o ID do documento é atribuído
                        Log.d("GestaoAulas", "carregarAulas: Mapeado: ID=${aula.id}, Nome=${aula.nome}")
                    }
                    aula // Retorna o objeto aula (ou null se toObject falhar)
                }

                aulas = listaDeAulas // ATRIBUI A LISTA COMPLETA AQUI
                Log.d("GestaoAulas", "carregarAulas: Lista 'aulas' populada com ${aulas.size} itens.")

                // Agora que a lista 'aulas' está preenchida, chame buscarAulas
                Log.d("GestaoAulas", "carregarAulas: Chamando buscarAulas() com query atual: '${edxBuscarAula.text.toString()}'")
                buscarAulas(edxBuscarAula.text.toString())
            }
            .addOnFailureListener { e ->
                Log.e("GestaoAulas", "carregarAulas: ERRO DE CONEXÃO ou PERMISSÃO: ${e.message}", e)
                Toast.makeText(requireContext(), "Erro ao carregar aulas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun buscarAulas(query: String) {
        Log.d("GestaoAulas", "buscarAulas: Recebida query: '$query'. Tamanho da lista original 'aulas': ${aulas.size}")

        val filteredList = if (query.isEmpty()) {
            aulas // Se a busca está vazia, usa a lista 'aulas' completa
        } else {
            aulas.filter {
                // Verifique se o campo 'nome' na sua data class 'Aula' corresponde ao nome do campo no Firestore
                it.nome.contains(query, ignoreCase = true) ||
                        it.instrutor.contains(query, ignoreCase = true) ||
                        it.diaDaSemana.contains(query, ignoreCase = true)
            }
        }

        Log.d("GestaoAulas", "buscarAulas: Tamanho da lista filtrada: ${filteredList.size}")

        val aulasAdapter = AulaAdapter(filteredList) { aula, acao ->
            when (acao) {
                AcoesMenuMais.VER_DETALHES -> {
                    val intentAulaDetalhes = Intent(requireContext(), AulaDetalhes::class.java)
                    intentAulaDetalhes.putExtra("aulaId", aula.id)
                    startActivity(intentAulaDetalhes)
                }
                AcoesMenuMais.EDITAR -> {
                    mostrarDialogEditarAula("Editar aula", aula)
                }
                AcoesMenuMais.EXCLUIR -> {
                    mostrarDialogExclusao("Confirmar exclusão", aula)
                }
            }
        }
        recyclerAulas.layoutManager = LinearLayoutManager(requireContext())
        recyclerAulas.adapter = aulasAdapter
    }

    private fun configurarSpinners(
        spinnerInstrutor: Spinner,
        spinnerDiaSemana: Spinner
    ) {
        // --- Instrutores ---
        fb.collection("users")
            .get()
            .addOnSuccessListener { result ->
                val instrutores = result.documents
                    .filter { it.getString("role") == "ADMIN" }
                    .map { it.getString("name") ?: "Sem nome" } // ou "email" se preferir

                context?.let { ctx ->
                    val adapterInstrutor = ArrayAdapter(
                        ctx,
                        android.R.layout.simple_spinner_item,
                        instrutores
                    )
                    adapterInstrutor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerInstrutor.adapter = adapterInstrutor
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao buscar instrutores: ", exception)
                Toast.makeText(context, "Erro ao carregar instrutores", Toast.LENGTH_SHORT).show()
            }

        // --- Dias da semana ---
        val diasSemana = listOf("Segunda", "Terca", "Quarta", "Quinta", "Sexta", "Sabado", "Domingo")
        context?.let { ctx ->
            val adapterDias = ArrayAdapter(ctx, android.R.layout.simple_spinner_item, diasSemana)
            adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerDiaSemana.adapter = adapterDias
        }
    }


    private fun configurarRecyclerEquipamentos(recyclerEquipamentos: RecyclerView, listaEquipamentos: MutableList<Equipamento>, equipamentoAdapter: SelecionaEquipamentosAdapter) {
        fb.collection("equipamentos")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val equipamento = Equipamento(
                        id = document.id,
                        nome = document.getString("nome") ?: "",
                        selecionado = false
                    )
                    listaEquipamentos.add(equipamento)
                }
                equipamentoAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao carregar equipamentos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}