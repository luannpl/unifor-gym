package com.example.unifor_gym.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unifor_gym.R
import com.example.unifor_gym.adapters.UsuarioAdapter
import com.example.unifor_gym.models.Usuario
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class GestaoUsuarios : Fragment(), UsuarioAdapter.OnUsuarioClickListener {

    private lateinit var recyclerUsuarios: RecyclerView
    private lateinit var edtBuscarUsuario: EditText
    private lateinit var fabAddUsuario: FloatingActionButton
    private lateinit var usuarioAdapter: UsuarioAdapter
    private lateinit var listaUsuarios: MutableList<Usuario>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_gestao_usuarios, container, false)

        recyclerUsuarios = view.findViewById(R.id.recyclerUsuarios)
        edtBuscarUsuario = view.findViewById(R.id.edtBuscarUsuario)
        fabAddUsuario = view.findViewById(R.id.fabAddUsuario)

        configurarRecyclerView()
        configurarBotoes()

        return view
    }

    private fun configurarRecyclerView() {
        listaUsuarios = gerarMockUsuarios().toMutableList()
        usuarioAdapter = UsuarioAdapter(listaUsuarios, this)
        recyclerUsuarios.layoutManager = LinearLayoutManager(requireContext())
        recyclerUsuarios.adapter = usuarioAdapter
    }

    private fun configurarBotoes() {
        fabAddUsuario.setOnClickListener {
            mostrarDialogAdicionarUsuario()
        }
    }

    private fun gerarMockUsuarios(): List<Usuario> {
        return listOf(
            Usuario(nome = "Matheus Lima", id = 1, email = "matheus@email.com", corAvatar = 0xFFFFCDD2.toInt()),
            Usuario(nome = "Arthur Dio.", id = 2, email = "arthur@email.com", corAvatar = 0xFFFC8E6C9.toInt()),
            Usuario(nome = "Paulo Luan", id = 3, email = "paulo@email.com", corAvatar = 0xFFFC8E6C9.toInt()),
            Usuario(nome = "Ana Clara", id = 4, email = "ana@email.com", corAvatar = 0xFFFC8E6C9.toInt()),
            Usuario(nome = "Thiago Marak", id = 5, email = "thiago@email.com", corAvatar = 0xFFFC8E6C9.toInt()),
            Usuario(nome = "Ronnison", id = 6, email = "ronnison@email.com", corAvatar = 0xFFFFCDD2.toInt())
        )
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

        // Calcular o X para ficar alinhado à direita do botão
        // Calcular o Y para ficar logo abaixo do botão
        window?.attributes?.x = 0  // Resetar o X
        window?.attributes?.y = 0  // Resetar o Y

        // Define a posição usando o Gravity em vez de coordenadas absolutas
        window?.setGravity(Gravity.TOP or Gravity.END)

        // Definir margens em relação à borda da tela
        // O popup vai aparecer alinhado com a posição do botão
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

    private fun mostrarDialogMontarTreino(usuario: Usuario) {
        val dialog = Dialog(requireContext(), R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_montar_treino)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        // Configurar texto do usuário
        val txtUsuarioTreino = dialog.findViewById<TextView>(R.id.txtUsuarioTreino)
        txtUsuarioTreino.text = "${usuario.nome} (${usuario.id})"

        // Botão de fechar
        val btnFechar = dialog.findViewById<ImageButton>(R.id.btnFecharMontarTreino)
        btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        // Configurar spinners
        configurarSpinnersTreino(dialog)

        // Botão de adicionar exercício
        val btnAdicionarExercicio = dialog.findViewById<Button>(R.id.btnAdicionarExercicio)
        btnAdicionarExercicio.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidade em desenvolvimento", Toast.LENGTH_SHORT).show()
            // Aqui você implementaria a lógica para adicionar mais um exercício
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

        // Spinners de exercícios
        val spinnerExercicio1 = dialog.findViewById<Spinner>(R.id.spinnerExercicio1)
        val spinnerExercicio2 = dialog.findViewById<Spinner>(R.id.spinnerExercicio2)
        val exercicios = arrayOf("Selecione", "Supino reto", "Agachamento", "Leg press", "Rosca direta", "Flexão", "Abdominal")
        val adapterExercicios = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, exercicios)
        spinnerExercicio1.adapter = adapterExercicios
        spinnerExercicio2.adapter = adapterExercicios

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

        // Spinners de descanso
        val spinnerDescanso1 = dialog.findViewById<Spinner>(R.id.spinnerDescanso1)
        val spinnerDescanso2 = dialog.findViewById<Spinner>(R.id.spinnerDescanso2)
        val descansos = arrayOf("30s", "45s", "60s", "90s", "120s")
        val adapterDescanso = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, descansos)
        spinnerDescanso1.adapter = adapterDescanso
        spinnerDescanso2.adapter = adapterDescanso
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
            val nome = edtNome.text.toString()
            val email = edtEmail.text.toString()
            val senha = edtSenha.text.toString()

            if (nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty()) {
                val novoUsuario = Usuario(
                    id = listaUsuarios.size + 1,
                    nome = nome,
                    email = email,
                    corAvatar = getRandomColor()
                )

                listaUsuarios.add(novoUsuario)
                usuarioAdapter.notifyItemInserted(listaUsuarios.size - 1)
                dialog.dismiss()
                Toast.makeText(requireContext(), "Usuário adicionado com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
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
            val nome = edtNome.text.toString()
            val email = edtEmail.text.toString()

            if (nome.isNotEmpty() && email.isNotEmpty()) {
                val index = listaUsuarios.indexOfFirst { it.id == usuario.id }
                if (index != -1) {
                    val usuarioAtualizado = Usuario(
                        id = usuario.id,
                        nome = nome,
                        email = email,
                        corAvatar = usuario.corAvatar
                    )

                    listaUsuarios[index] = usuarioAtualizado
                    usuarioAdapter.notifyItemChanged(index)
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Usuário atualizado com sucesso", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun mostrarDialogConfirmacaoExclusao(usuario: Usuario) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_excluir_confirmacao)
        dialog.setCancelable(true)

        val window = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnCancelar = dialog.findViewById<Button>(R.id.btnCancelarExcluirUsuario)
        val btnConfirmar = dialog.findViewById<Button>(R.id.btnConfirmarExcluirUsuario)

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirmar.setOnClickListener {
            val index = listaUsuarios.indexOfFirst { it.id == usuario.id }
            if (index != -1) {
                listaUsuarios.removeAt(index)
                usuarioAdapter.notifyItemRemoved(index)
                dialog.dismiss()
                Toast.makeText(requireContext(), "Usuário excluído com sucesso", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
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