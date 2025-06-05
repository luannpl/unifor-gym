package com.example.unifor_gym.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.UsuarioAdapter
import com.example.unifor_gym.models.UserProfile
import com.example.unifor_gym.models.UserRole
import com.example.unifor_gym.models.Usuario
import com.google.android.material.textfield.TextInputEditText
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.UUID

class GestaoUsuarios : Fragment(), UsuarioAdapter.OnUsuarioClickListener {

    private lateinit var recyclerUsuarios: RecyclerView
    private lateinit var edtBuscarUsuario: EditText
    private lateinit var btnAdicionarUsuario: Button
    private lateinit var usuarioAdapter: UsuarioAdapter
    private lateinit var fb: FirebaseFirestore
    private var listaUsuarios: MutableList<Usuario> = mutableListOf()
    private var todosUsuarios: List<Usuario> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_gestao_usuarios, container, false)

        recyclerUsuarios = view.findViewById(R.id.recyclerUsuarios)
        edtBuscarUsuario = view.findViewById(R.id.edtBuscarUsuario)
        btnAdicionarUsuario = view.findViewById(R.id.btnAdicionarUsuario)
        fb = Firebase.firestore

        configurarRecyclerView()
        configurarBotoes()
        configurarBusca()
        carregarUsuariosDoFirebase()

        return view
    }

    private fun configurarRecyclerView() {
        usuarioAdapter = UsuarioAdapter(listaUsuarios, this)
        recyclerUsuarios.layoutManager = LinearLayoutManager(requireContext())
        recyclerUsuarios.adapter = usuarioAdapter
    }

    private fun configurarBotoes() {
        btnAdicionarUsuario.setOnClickListener {
            mostrarDialogAdicionarUsuario()
        }
    }

    private fun configurarBusca() {
        edtBuscarUsuario.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarUsuarios(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun carregarUsuariosDoFirebase() {
        Log.d("GestaoUsuarios", "carregarUsuarios: Iniciando busca no Firestore...")

        fb.collection("users")
            .get()
            .addOnSuccessListener { result ->
                Log.d("GestaoUsuarios", "carregarUsuarios: Sucesso na busca. Documentos encontrados: ${result.size()}")

                if (result.isEmpty) {
                    Log.w("GestaoUsuarios", "carregarUsuarios: Nenhum usuário encontrado na coleção 'users'.")
                    Toast.makeText(requireContext(), "Nenhum usuário cadastrado.", Toast.LENGTH_LONG).show()
                }

                val listaDeUsuarios = result.mapNotNull { doc ->
                    try {
                        val userProfile = doc.toObject(UserProfile::class.java)
                        if (userProfile == null) {
                            Log.e("GestaoUsuarios", "carregarUsuarios: Erro ao mapear documento ${doc.id} para UserProfile.")
                            null
                        } else {
                            // Converter UserProfile para Usuario (compatibilidade com adapter existente)
                            val usuario = Usuario(
                                id = doc.id.hashCode(), // Usar hash do document ID como int
                                nome = userProfile.name,
                                email = userProfile.email,
                                corAvatar = getRandomColor()
                            )
                            Log.d("GestaoUsuarios", "carregarUsuarios: Mapeado: ID=${usuario.id}, Nome=${usuario.nome}")
                            usuario
                        }
                    } catch (e: Exception) {
                        Log.e("GestaoUsuarios", "Erro ao converter documento ${doc.id}", e)
                        null
                    }
                }

                todosUsuarios = listaDeUsuarios
                Log.d("GestaoUsuarios", "carregarUsuarios: Lista 'todosUsuarios' populada com ${todosUsuarios.size} itens.")

                buscarUsuarios(edtBuscarUsuario.text.toString())
            }
            .addOnFailureListener { e ->
                Log.e("GestaoUsuarios", "carregarUsuarios: ERRO DE CONEXÃO: ${e.message}", e)
                Toast.makeText(requireContext(), "Erro ao carregar usuários: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getRandomColor(): Int {
        val colors = arrayOf(
            0xFFE57373.toInt(), 0xFFF06292.toInt(), 0xFFBA68C8.toInt(),
            0xFF9575CD.toInt(), 0xFF7986CB.toInt(), 0xFF64B5F6.toInt(),
            0xFF4FC3F7.toInt(), 0xFF4DD0E1.toInt(), 0xFF4DB6AC.toInt(),
            0xFF81C784.toInt(), 0xFFAED581.toInt(), 0xFFFF8A65.toInt()
        )
        return colors.random()
    }

    private fun buscarUsuarios(query: String) {
        Log.d("GestaoUsuarios", "buscarUsuarios: Recebida query: '$query'. Tamanho da lista original: ${todosUsuarios.size}")

        val filteredList = if (query.isEmpty()) {
            todosUsuarios
        } else {
            todosUsuarios.filter {
                it.nome.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true) ||
                        it.id.toString().contains(query, ignoreCase = true)
            }
        }

        Log.d("GestaoUsuarios", "buscarUsuarios: Tamanho da lista filtrada: ${filteredList.size}")

        listaUsuarios.clear()
        listaUsuarios.addAll(filteredList)
        usuarioAdapter.notifyDataSetChanged()
    }

    override fun onMoreButtonClick(usuario: Usuario, view: View) {
        mostrarMenuOpcoes(usuario, view)
    }

    private fun mostrarMenuOpcoes(usuario: Usuario, anchorView: View) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_opcoes_usuario)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Obter a posição exata do botão
        val location = IntArray(2)
        anchorView.getLocationInWindow(location)

        window?.setGravity(Gravity.TOP or Gravity.END)

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val marginRight = screenWidth - location[0] - anchorView.width - 20
        val marginTop = location[1] - 10

        window?.attributes?.x = marginRight
        window?.attributes?.y = marginTop

        window?.setBackgroundDrawableResource(android.R.color.transparent)

        val layoutMontarTreino = dialog.findViewById<LinearLayout>(R.id.layoutMontarTreino)
        val layoutEditar = dialog.findViewById<LinearLayout>(R.id.layoutEditar)
        val layoutExcluir = dialog.findViewById<LinearLayout>(R.id.layoutExcluir)

        layoutMontarTreino.setOnClickListener {
            dialog.dismiss()
            mostrarDialogMontarTreino(usuario)
        }

        layoutEditar.setOnClickListener {
            dialog.dismiss()
            mostrarDialogEditarUsuario(usuario)
        }

        layoutExcluir.setOnClickListener {
            dialog.dismiss()
            mostrarDialogConfirmacaoExclusao(usuario)
        }

        dialog.show()
    }

    private fun mostrarDialogAdicionarUsuario() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_adicionar_usuario)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val edtNome = dialog.findViewById<TextInputEditText>(R.id.edtNomeUsuario)
        val edtEmail = dialog.findViewById<TextInputEditText>(R.id.edtEmailUsuario)
        val edtSenha = dialog.findViewById<TextInputEditText>(R.id.edtSenhaUsuario)
        val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelarAddUsuario)
        val btnSalvar = dialog.findViewById<Button>(R.id.btnSalvarUsuario)

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnSalvar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString().trim() // Não será usado, mas mantemos por compatibilidade

            if (nome.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha pelo menos nome e email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar se email já existe
            fb.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        Toast.makeText(requireContext(), "Este email já está cadastrado", Toast.LENGTH_SHORT).show()
                    } else {
                        // Criar novo UserProfile
                        val novoUserProfile = UserProfile(
                            uid = UUID.randomUUID().toString(), // Gerar UID único
                            name = nome,
                            email = email,
                            role = UserRole.USER, // Sempre USER conforme especificado
                            createdAt = System.currentTimeMillis()
                        )

                        // Salvar no Firestore
                        fb.collection("users")
                            .add(novoUserProfile)
                            .addOnSuccessListener { documentReference ->
                                Toast.makeText(requireContext(), "Usuário adicionado com sucesso", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                carregarUsuariosDoFirebase() // Recarregar lista
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Erro ao adicionar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("GestaoUsuarios", "Erro ao adicionar usuário", e)
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao verificar email: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("GestaoUsuarios", "Erro ao verificar email", e)
                }
        }

        dialog.show()
    }

    private fun mostrarDialogEditarUsuario(usuario: Usuario) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_editar_usuario)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val edtNome = dialog.findViewById<TextInputEditText>(R.id.edtEditarNomeUsuario)
        val edtEmail = dialog.findViewById<TextInputEditText>(R.id.edtEditarEmailUsuario)
        val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelarEditarUsuario)
        val btnAtualizar = dialog.findViewById<Button>(R.id.btnAtualizarUsuario)

        // Preencher com os dados atuais
        edtNome.setText(usuario.nome)
        edtEmail.setText(usuario.email)

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnAtualizar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val email = edtEmail.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Encontrar o documento do usuário no Firestore
            fb.collection("users")
                .whereEqualTo("email", usuario.email) // Buscar pelo email original
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val documentId = querySnapshot.documents[0].id

                        // Atualizar o documento
                        val dadosAtualizados = mapOf(
                            "name" to nome,
                            "email" to email,
                            "updatedAt" to FieldValue.serverTimestamp()
                        )

                        fb.collection("users")
                            .document(documentId)
                            .update(dadosAtualizados)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Usuário atualizado com sucesso", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                carregarUsuariosDoFirebase() // Recarregar lista
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(requireContext(), "Erro ao atualizar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("GestaoUsuarios", "Erro ao atualizar usuário", e)
                            }
                    } else {
                        Toast.makeText(requireContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Erro ao buscar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("GestaoUsuarios", "Erro ao buscar usuário", e)
                }
        }

        dialog.show()
    }

    private fun mostrarDialogConfirmacaoExclusao(usuario: Usuario) {
        AlertDialog.Builder(requireContext())
            .setTitle("Excluir Usuário")
            .setMessage("Tem certeza que deseja excluir o usuário '${usuario.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                // Encontrar e excluir o documento do usuário no Firestore
                fb.collection("users")
                    .whereEqualTo("email", usuario.email)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val documentId = querySnapshot.documents[0].id

                            fb.collection("users")
                                .document(documentId)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Usuário excluído com sucesso", Toast.LENGTH_SHORT).show()
                                    carregarUsuariosDoFirebase() // Recarregar lista
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(requireContext(), "Erro ao excluir usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("GestaoUsuarios", "Erro ao excluir usuário", e)
                                }
                        } else {
                            Toast.makeText(requireContext(), "Usuário não encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Erro ao buscar usuário: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.e("GestaoUsuarios", "Erro ao buscar usuário", e)
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogMontarTreino(usuario: Usuario) {
        val dialog = Dialog(requireContext(), R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_montar_treino)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Configurar texto do usuário
        val txtUsuarioTreino = dialog.findViewById<TextView>(R.id.txtUsuarioTreino)
        txtUsuarioTreino.text = "${usuario.nome} (${usuario.email})"

        // Botão de fechar
        val btnFechar = dialog.findViewById<ImageButton>(R.id.btnFecharMontarTreino)
        btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        // Configurar spinners
        configurarSpinnersTreino(dialog)

        // Carregar exercícios nos spinners
        carregarExerciciosParaSpinners(dialog)

        // Botão salvar treino (usar o botão adicionar exercício como salvar por simplicidade)
        val btnAdicionarExercicio = dialog.findViewById<Button>(R.id.btnAdicionarExercicio)
        btnAdicionarExercicio.setText("Salvar Treino")
        btnAdicionarExercicio.setOnClickListener {
            salvarTreino(dialog, usuario)
        }

        dialog.show()
    }

    private fun configurarSpinnersTreino(dialog: Dialog) {
        // Spinner de tipo de treino
        val spinnerTipoTreino = dialog.findViewById<Spinner>(R.id.spinnerTipoTreino)
        val tiposTreino = arrayOf("Musculação", "Cardio", "Funcional", "Crossfit", "Yoga")
        val adapterTipoTreino = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tiposTreino)
        spinnerTipoTreino.adapter = adapterTipoTreino

        // Spinner de dias da semana
        val spinnerDiasSemana = dialog.findViewById<Spinner>(R.id.spinnerDiasSemana)
        val diasSemana = arrayOf("Segunda e Quinta", "Terça e Sexta", "Quarta e Sábado", "Todos os dias")
        val adapterDiasSemana = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, diasSemana)
        spinnerDiasSemana.adapter = adapterDiasSemana

        // Spinners de séries
        val spinnerSeries1 = dialog.findViewById<Spinner>(R.id.spinnerSeries1)
        val spinnerSeries2 = dialog.findViewById<Spinner>(R.id.spinnerSeries2)
        val series = arrayOf("2", "3", "4", "5")
        val adapterSeries = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, series)
        spinnerSeries1.adapter = adapterSeries
        spinnerSeries2.adapter = adapterSeries

        // Spinners de repetições
        val spinnerReps1 = dialog.findViewById<Spinner>(R.id.spinnerReps1)
        val spinnerReps2 = dialog.findViewById<Spinner>(R.id.spinnerReps2)
        val repeticoes = arrayOf("5", "8", "10", "12", "15", "20")
        val adapterReps = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, repeticoes)
        spinnerReps1.adapter = adapterReps
        spinnerReps2.adapter = adapterReps

        // Spinners de carga
        val spinnerCarga1 = dialog.findViewById<Spinner>(R.id.spinnerCarga1)
        val spinnerCarga2 = dialog.findViewById<Spinner>(R.id.spinnerCarga2)
        val cargas = arrayOf("5kg", "10kg", "15kg", "20kg", "25kg", "30kg", "35kg", "40kg", "45kg", "50kg", "60kg", "70kg", "80kg", "90kg", "100kg")
        val adapterCarga = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, cargas)
        spinnerCarga1.adapter = adapterCarga
        spinnerCarga2.adapter = adapterCarga

        // Spinners de descanso
        val spinnerDescanso1 = dialog.findViewById<Spinner>(R.id.spinnerDescanso1)
        val spinnerDescanso2 = dialog.findViewById<Spinner>(R.id.spinnerDescanso2)
        val descansos = arrayOf("30s", "45s", "60s", "90s", "120s")
        val adapterDescanso = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, descansos)
        spinnerDescanso1.adapter = adapterDescanso
        spinnerDescanso2.adapter = adapterDescanso
    }

    private fun carregarExerciciosParaSpinners(dialog: Dialog) {
        fb.collection("exercicios")
            .get()
            .addOnSuccessListener { result ->
                val exercicios = mutableListOf<String>()
                exercicios.add("Selecione") // Opção padrão

                for (document in result) {
                    val nomeExercicio = document.getString("nome")
                    if (!nomeExercicio.isNullOrBlank()) {
                        exercicios.add(nomeExercicio)
                    }
                }

                // Configurar spinners de exercícios
                val spinnerExercicio1 = dialog.findViewById<Spinner>(R.id.spinnerExercicio1)
                val spinnerExercicio2 = dialog.findViewById<Spinner>(R.id.spinnerExercicio2)
                val adapterExercicios = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, exercicios)
                spinnerExercicio1.adapter = adapterExercicios
                spinnerExercicio2.adapter = adapterExercicios
            }
            .addOnFailureListener { e ->
                Log.e("GestaoUsuarios", "Erro ao carregar exercícios", e)
                Toast.makeText(requireContext(), "Erro ao carregar exercícios", Toast.LENGTH_SHORT).show()
            }
    }

    private fun salvarTreino(dialog: Dialog, usuario: Usuario) {
        // Obter valores dos campos
        val edtNomeTreino = dialog.findViewById<EditText>(R.id.edtNomeTreino)
        val spinnerTipoTreino = dialog.findViewById<Spinner>(R.id.spinnerTipoTreino)
        val spinnerDiasSemana = dialog.findViewById<Spinner>(R.id.spinnerDiasSemana)

        val spinnerExercicio1 = dialog.findViewById<Spinner>(R.id.spinnerExercicio1)
        val spinnerSeries1 = dialog.findViewById<Spinner>(R.id.spinnerSeries1)
        val spinnerReps1 = dialog.findViewById<Spinner>(R.id.spinnerReps1)
        val spinnerCarga1 = dialog.findViewById<Spinner>(R.id.spinnerCarga1)
        val spinnerDescanso1 = dialog.findViewById<Spinner>(R.id.spinnerDescanso1)

        val spinnerExercicio2 = dialog.findViewById<Spinner>(R.id.spinnerExercicio2)
        val spinnerSeries2 = dialog.findViewById<Spinner>(R.id.spinnerSeries2)
        val spinnerReps2 = dialog.findViewById<Spinner>(R.id.spinnerReps2)
        val spinnerCarga2 = dialog.findViewById<Spinner>(R.id.spinnerCarga2)
        val spinnerDescanso2 = dialog.findViewById<Spinner>(R.id.spinnerDescanso2)

        val nomeTreino = edtNomeTreino.text.toString().trim()
        val tipoTreino = spinnerTipoTreino.selectedItem.toString()
        val diasSemana = spinnerDiasSemana.selectedItem.toString()

        if (nomeTreino.isEmpty()) {
            Toast.makeText(requireContext(), "Digite o nome do treino", Toast.LENGTH_SHORT).show()
            return
        }

        // Montar lista de exercícios
        val exercicios = mutableListOf<Map<String, Any>>()

        // Exercício 1
        val exercicio1 = spinnerExercicio1.selectedItem.toString()
        if (exercicio1 != "Selecione") {
            exercicios.add(mapOf(
                "nome" to exercicio1,
                "series" to spinnerSeries1.selectedItem.toString(),
                "repeticoes" to spinnerReps1.selectedItem.toString(),
                "carga" to spinnerCarga1.selectedItem.toString(),
                "descanso" to spinnerDescanso1.selectedItem.toString()
            ))
        }

        // Exercício 2
        val exercicio2 = spinnerExercicio2.selectedItem.toString()
        if (exercicio2 != "Selecione") {
            exercicios.add(mapOf(
                "nome" to exercicio2,
                "series" to spinnerSeries2.selectedItem.toString(),
                "repeticoes" to spinnerReps2.selectedItem.toString(),
                "carga" to spinnerCarga2.selectedItem.toString(),
                "descanso" to spinnerDescanso2.selectedItem.toString()
            ))
        }

        if (exercicios.isEmpty()) {
            Toast.makeText(requireContext(), "Selecione pelo menos um exercício", Toast.LENGTH_SHORT).show()
            return
        }

        // Criar treino para salvar no Firebase
        val treinoData = hashMapOf(
            "titulo" to nomeTreino,
            "usuarioEmail" to usuario.email, // Usar email para identificar usuário
            "tipoTreino" to tipoTreino,
            "diasSemana" to diasSemana,
            "exercicios" to exercicios,
            "dataCriacao" to FieldValue.serverTimestamp()
        )

        // Salvar no Firestore
        fb.collection("treinos")
            .add(treinoData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Treino criado com sucesso!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Erro ao criar treino: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("GestaoUsuarios", "Erro ao salvar treino", e)
            }
    }
    val colors = arrayOf(
        0xFFE57373.toInt(), 0xFFF06292.toInt(), 0xFFBA68C8.toInt(),
        0xFF9575CD.toInt(), 0xFF7986CB.toInt(), 0xFF64B5F6.toInt(),
        0xFF4FC3F7.toInt(), 0xFF4DD0E1.toInt(), 0xFF4DB6AC.toInt(),
        0xFF81C784.toInt(), 0xFFAED581.toInt(), 0xFFFF8A65.toInt()
    )
}