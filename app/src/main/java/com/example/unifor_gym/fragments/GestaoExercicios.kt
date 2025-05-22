package com.example.unifor_gym.fragments

import com.google.firebase.firestore.FirebaseFirestore
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.activities.ExercicioDetalhes
import com.example.unifor_gym.adapters.ExercicioAdapter
import com.example.unifor_gym.models.AcoesMenuMais
import com.example.unifor_gym.models.Exercicio

class GestaoExercicios : Fragment() {
    private lateinit var recyclerExercicios: RecyclerView
    private lateinit var btnAdicionarExercicio: Button
    private lateinit var edtBuscarExercicio: EditText
    private lateinit var listaExercicios: MutableList<Exercicio>
    private lateinit var exercicioAdapter: ExercicioAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gestao_exercicios, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()

        // Inicializar componentes
        recyclerExercicios = view.findViewById(R.id.recyclerViewGestaoExercicios)
        btnAdicionarExercicio = view.findViewById(R.id.btnAdicionarExercicio)
        edtBuscarExercicio = view.findViewById(R.id.edtBuscarExercicio)

        // Inicializar lista vazia
        listaExercicios = mutableListOf()

        // Configurar RecyclerView
        configureRecyclerView()

        // Configurar botões
        configurarBotoes()

        // Carregar exercícios do Firebase
        carregarExerciciosDoFirebase()
    }

    private fun configureRecyclerView() {
        recyclerExercicios.layoutManager = LinearLayoutManager(requireContext())
        exercicioAdapter = ExercicioAdapter(listaExercicios) { exercicio, acao ->
            handleExercicioAction(exercicio, acao)
        }
        recyclerExercicios.adapter = exercicioAdapter
    }

    private fun configurarBotoes() {
        btnAdicionarExercicio.setOnClickListener {
            mostrarDialogAdicionarExercicio()
        }
    }

    private fun handleExercicioAction(exercicio: Exercicio, acao: AcoesMenuMais) {
        when (acao) {
            AcoesMenuMais.VER_DETALHES -> {
                val intent = Intent(requireContext(), ExercicioDetalhes::class.java)
                intent.putExtra("exercicio", exercicio)
                startActivity(intent)
            }
            AcoesMenuMais.EDITAR -> {
                mostrarDialogEditarExercicio(exercicio)
            }
            AcoesMenuMais.EXCLUIR -> {
                mostrarDialogConfirmacaoExclusao(exercicio)
            }
        }
    }

    private fun carregarExerciciosDoFirebase() {
        firestore.collection("exercicios")
            .get()
            .addOnSuccessListener { documents ->
                listaExercicios.clear()

                for (document in documents) {
                    val exercicio = document.toObject(Exercicio::class.java)
                    exercicio.firebaseId = document.id // Definir o ID do Firebase
                    listaExercicios.add(exercicio)
                }

                exercicioAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao carregar exercícios: ${e.message}", Toast.LENGTH_SHORT).show()
                // Usar dados mock como fallback
                exercicioAdapter.notifyDataSetChanged()
            }
    }


    private fun mostrarDialogAdicionarExercicio() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_adicionar_exercicio)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Obter referências dos componentes
        val btnFechar = dialog.findViewById<ImageButton>(R.id.btnFecharAddExercicio)
        val edtNome = dialog.findViewById<EditText>(R.id.edtNomeExercicio)
        val edtAparelhos = dialog.findViewById<EditText>(R.id.edtAparelhos)
        val spinnerDificuldade = dialog.findViewById<Spinner>(R.id.spinnerDificuldade)
        val edtInstrucoes = dialog.findViewById<EditText>(R.id.edtInstrucoes)
        val edtLinkVideo = dialog.findViewById<EditText>(R.id.edtLinkVideo)
        val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelarExercicio)
        val btnSalvar = dialog.findViewById<Button>(R.id.btnSalvarExercicio)

        // Obter checkboxes de categorias
        val checkPeito = dialog.findViewById<CheckBox>(R.id.checkPeito)
        val checkBracos = dialog.findViewById<CheckBox>(R.id.checkBracos)
        val checkCostas = dialog.findViewById<CheckBox>(R.id.checkCostas)
        val checkPernas = dialog.findViewById<CheckBox>(R.id.checkPernas)
        val checkOmbros = dialog.findViewById<CheckBox>(R.id.checkOmbros)
        val checkCore = dialog.findViewById<CheckBox>(R.id.checkCore)
        val checkCardio = dialog.findViewById<CheckBox>(R.id.checkCardio)
        val checkCorpoInteiro = dialog.findViewById<CheckBox>(R.id.checkCorpoInteiro)
        val checkFuncional = dialog.findViewById<CheckBox>(R.id.checkFuncional)

        // Configurar spinner de dificuldade
        val dificuldades = arrayOf("Iniciante", "Intermediário", "Avançado")
        val adapterDificuldade = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, dificuldades)
        spinnerDificuldade.adapter = adapterDificuldade

        // Configurar botões
        btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnSalvar.setOnClickListener {
            val nome = edtNome.text.toString()
            val aparelhosText = edtAparelhos.text.toString()
            val dificuldade = spinnerDificuldade.selectedItem.toString()
            val instrucoes = edtInstrucoes.text.toString()
            val linkVideo = edtLinkVideo.text.toString()

            // Coletar categorias selecionadas
            val categoriasSelected = mutableListOf<String>()
            if (checkPeito.isChecked) categoriasSelected.add("Peito")
            if (checkBracos.isChecked) categoriasSelected.add("Braços")
            if (checkCostas.isChecked) categoriasSelected.add("Costas")
            if (checkPernas.isChecked) categoriasSelected.add("Pernas")
            if (checkOmbros.isChecked) categoriasSelected.add("Ombros")
            if (checkCore.isChecked) categoriasSelected.add("Core")
            if (checkCardio.isChecked) categoriasSelected.add("Cardio")
            if (checkCorpoInteiro.isChecked) categoriasSelected.add("Corpo Inteiro")
            if (checkFuncional.isChecked) categoriasSelected.add("Funcional")

            // Validar entrada
            if (nome.isBlank()) {
                Toast.makeText(requireContext(), "Por favor, informe o nome do exercício", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (categoriasSelected.isEmpty()) {
                Toast.makeText(requireContext(), "Selecione pelo menos uma categoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Selecionar grupo muscular principal (primeira categoria)
            val grupoMuscularPrincipal = categoriasSelected.first()

            // Dividir aparelhos por vírgula
            val aparelhosLista = aparelhosText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            // Criar novo exercício SEM firebaseId ainda
            val novoExercicio = Exercicio(
                nome = nome,
                grupoMuscular = grupoMuscularPrincipal,
                dificuldade = dificuldade,
                categorias = categoriasSelected,
                aparelhos = aparelhosLista,
                instrucoes = instrucoes,
                videoUrl = if (linkVideo.isNotBlank()) linkVideo else null
            )

            // Salvar no Firestore
            firestore.collection("exercicios")
                .add(novoExercicio)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(requireContext(), "Exercício adicionado com sucesso", Toast.LENGTH_SHORT).show()

                    // Criar exercício com o ID real do Firebase
                    val exercicioComFirebaseId = novoExercicio.copy(
                        firebaseId = documentReference.id
                    )

                    // Adicionar à lista local
                    listaExercicios.add(exercicioComFirebaseId)
                    exercicioAdapter.notifyItemInserted(listaExercicios.size - 1)
                    dialog.dismiss()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao adicionar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }

    private fun mostrarDialogEditarExercicio(exercicio: Exercicio) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_editar_exercicio)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Obter referências dos componentes
        val btnFechar = dialog.findViewById<ImageButton>(R.id.btnFecharEditarExercicio)
        val edtNome = dialog.findViewById<EditText>(R.id.edtEditarNomeExercicio)
        val edtAparelhos = dialog.findViewById<EditText>(R.id.edtEditarAparelhos)
        val spinnerDificuldade = dialog.findViewById<Spinner>(R.id.spinnerEditarDificuldade)
        val edtInstrucoes = dialog.findViewById<EditText>(R.id.edtEditarInstrucoes)
        val edtLinkVideo = dialog.findViewById<EditText>(R.id.edtEditarLinkVideo)
        val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelarEditarExercicio)
        val btnSalvar = dialog.findViewById<Button>(R.id.btnSalvarEditarExercicio)

        // Obter checkboxes de categorias
        val checkPeito = dialog.findViewById<CheckBox>(R.id.checkEditarPeito)
        val checkBracos = dialog.findViewById<CheckBox>(R.id.checkEditarBracos)
        val checkCostas = dialog.findViewById<CheckBox>(R.id.checkEditarCostas)
        val checkPernas = dialog.findViewById<CheckBox>(R.id.checkEditarPernas)
        val checkOmbros = dialog.findViewById<CheckBox>(R.id.checkEditarOmbros)
        val checkCore = dialog.findViewById<CheckBox>(R.id.checkEditarCore)
        val checkCardio = dialog.findViewById<CheckBox>(R.id.checkEditarCardio)
        val checkCorpoInteiro = dialog.findViewById<CheckBox>(R.id.checkEditarCorpoInteiro)
        val checkFuncional = dialog.findViewById<CheckBox>(R.id.checkEditarFuncional)

        // Preencher com os dados do exercício
        edtNome.setText(exercicio.nome)
        edtAparelhos.setText(exercicio.aparelhos.joinToString(", "))
        edtInstrucoes.setText(exercicio.instrucoes)
        edtLinkVideo.setText(exercicio.videoUrl ?: "")

        // Marcar checkboxes das categorias
        if (exercicio.categorias.contains("Peito")) checkPeito.isChecked = true
        if (exercicio.categorias.contains("Braços")) checkBracos.isChecked = true
        if (exercicio.categorias.contains("Costas")) checkCostas.isChecked = true
        if (exercicio.categorias.contains("Pernas")) checkPernas.isChecked = true
        if (exercicio.categorias.contains("Ombros")) checkOmbros.isChecked = true
        if (exercicio.categorias.contains("Core")) checkCore.isChecked = true
        if (exercicio.categorias.contains("Cardio")) checkCardio.isChecked = true
        if (exercicio.categorias.contains("Corpo Inteiro")) checkCorpoInteiro.isChecked = true
        if (exercicio.categorias.contains("Funcional")) checkFuncional.isChecked = true

        // Configurar spinner de dificuldade
        val dificuldades = arrayOf("Iniciante", "Intermediário", "Avançado")
        val adapterDificuldade = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, dificuldades)
        spinnerDificuldade.adapter = adapterDificuldade

        // Selecionar a dificuldade atual
        val dificuldadeIndex = dificuldades.indexOf(exercicio.dificuldade)
        if (dificuldadeIndex != -1) {
            spinnerDificuldade.setSelection(dificuldadeIndex)
        }

        // Configurar botões
        btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnSalvar.setOnClickListener {
            val nome = edtNome.text.toString()
            val dificuldade = spinnerDificuldade.selectedItem.toString()
            val aparelhosText = edtAparelhos.text.toString()
            val instrucoes = edtInstrucoes.text.toString()
            val linkVideo = edtLinkVideo.text.toString()

            // Coletar categorias selecionadas
            val categoriasSelected = mutableListOf<String>()
            if (checkPeito.isChecked) categoriasSelected.add("Peito")
            if (checkBracos.isChecked) categoriasSelected.add("Braços")
            if (checkCostas.isChecked) categoriasSelected.add("Costas")
            if (checkPernas.isChecked) categoriasSelected.add("Pernas")
            if (checkOmbros.isChecked) categoriasSelected.add("Ombros")
            if (checkCore.isChecked) categoriasSelected.add("Core")
            if (checkCardio.isChecked) categoriasSelected.add("Cardio")
            if (checkCorpoInteiro.isChecked) categoriasSelected.add("Corpo Inteiro")
            if (checkFuncional.isChecked) categoriasSelected.add("Funcional")

            // Validar entrada
            if (nome.isBlank()) {
                Toast.makeText(requireContext(), "Por favor, informe o nome do exercício", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (categoriasSelected.isEmpty()) {
                Toast.makeText(requireContext(), "Selecione pelo menos uma categoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Selecionar grupo muscular principal (primeira categoria)
            val grupoMuscularPrincipal = categoriasSelected.first()

            // Dividir aparelhos por vírgula
            val aparelhosLista = aparelhosText.split(",").map { it.trim() }.filter { it.isNotEmpty() }

            // Criar exercício atualizado mantendo o firebaseId
            val exercicioAtualizado = Exercicio(
                id = exercicio.id,
                firebaseId = exercicio.firebaseId, // Manter o mesmo firebaseId
                nome = nome,
                grupoMuscular = grupoMuscularPrincipal,
                dificuldade = dificuldade,
                categorias = categoriasSelected,
                aparelhos = aparelhosLista,
                instrucoes = instrucoes,
                videoUrl = if (linkVideo.isNotBlank()) linkVideo else null
            )

            // Atualizar no Firebase usando o firebaseId
            exercicio.firebaseId?.let { firebaseId ->
                firestore.collection("exercicios").document(firebaseId)
                    .set(exercicioAtualizado)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Exercício atualizado com sucesso", Toast.LENGTH_SHORT).show()

                        // Atualizar na lista local
                        val index = listaExercicios.indexOfFirst { it.firebaseId == firebaseId }
                        if (index != -1) {
                            listaExercicios[index] = exercicioAtualizado
                            exercicioAdapter.notifyItemChanged(index)
                        }
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Erro ao atualizar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } ?: run {
                Toast.makeText(requireContext(), "ID do Firebase não encontrado", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun mostrarDialogConfirmacaoExclusao(exercicio: Exercicio) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Exercício")
            .setMessage("Tem certeza que deseja excluir o exercício '${exercicio.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                exercicio.firebaseId?.let { firebaseId ->
                    firestore.collection("exercicios").document(firebaseId)
                        .delete()
                        .addOnSuccessListener {
                            // Remover da lista local
                            val index = listaExercicios.indexOfFirst { it.firebaseId == firebaseId }
                            if (index != -1) {
                                listaExercicios.removeAt(index)
                                exercicioAdapter.notifyItemRemoved(index)
                            }
                            Toast.makeText(requireContext(), "Exercício excluído com sucesso", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Erro ao excluir exercício: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } ?: run {
                    Toast.makeText(requireContext(), "ID do Firebase não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}