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
            Toast.makeText(requireContext(), "Funcionalidade de montar treino não implementada", Toast.LENGTH_SHORT).show()
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

    private fun getRandomColor(): Int {
        val colors = arrayOf(
            0xFFE57373.toInt(), 0xFFF06292.toInt(), 0xFFBA68C8.toInt(),
            0xFF9575CD.toInt(), 0xFF7986CB.toInt(), 0xFF64B5F6.toInt(),
            0xFF4FC3F7.toInt(), 0xFF4DD0E1.toInt(), 0xFF4DB6AC.toInt(),
            0xFF81C784.toInt(), 0xFFAED581.toInt(), 0xFFFF8A65.toInt()
        )
        return colors.random()
    }
}